package com.example.lockin.service

import android.accessibilityservice.AccessibilityService
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import com.example.lockin.data.local.DataStoreManager
import com.example.lockin.domain.repository.LockSessionRepository
import com.example.lockin.ui.screens.BlockingActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import javax.inject.Inject

private const val TAG = "LockA11y"

@AndroidEntryPoint
class LockAccessibilityService : AccessibilityService() {

    @Inject
    lateinit var lockSessionRepository: LockSessionRepository

    private val job    = SupervisorJob()
    private val scope  = CoroutineScope(Dispatchers.IO + job)

    private var trackingJob: Job? = null
    private var trackedPackage    = ""
    private var trackStartTime    = 0L

    private var lastLaunchPkg  = ""
    private var lastLaunchTime = 0L

    // Lazy DataStore reference so we can self-heal if ProtectionState was cleared
    private val dataStore by lazy { DataStoreManager(applicationContext) }

    // ── Accessibility Lifecycle ───────────────────────────────────────────────

    override fun onServiceConnected() {
        super.onServiceConnected()
        Log.d(TAG, "Service connected — ensuring ProtectionState is loaded")
        // Self-heal: restore from DataStore if ProtectionState was wiped (e.g., first start)
        scope.launch { ensureStateLoaded() }
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event?.eventType != AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) return
        val pkg = event.packageName?.toString() ?: return

        val now = System.currentTimeMillis()

        // If this package is HARD LOCKED, always re-block — even if it's the same package.
        // This prevents the user from returning to the blocked app after dismissing BlockingActivity.
        if (ProtectionState.isHardLocked(pkg)) {
            scope.launch { handleForegroundChange(pkg) }
            return
        }

        if (pkg == trackedPackage) return  // already tracking, not locked → skip

        scope.launch { handleForegroundChange(pkg) }
    }

    override fun onInterrupt() = Unit
    override fun onDestroy() { job.cancel() }

    // ── Core Logic ────────────────────────────────────────────────────────────

    private suspend fun handleForegroundChange(newPkg: String) {
        val now = System.currentTimeMillis()

        // 1. Finalize elapsed usage for the previously tracked package
        if (trackedPackage.isNotEmpty() && trackStartTime > 0L) {
            if (!isIgnored(trackedPackage)) {
                ProtectionState.addUsage(trackedPackage, now - trackStartTime)
            }
        }

        // 2. Switch tracking to the new package
        trackingJob?.cancel()
        trackedPackage = newPkg
        trackStartTime = now

        if (isIgnored(newPkg)) {
            Log.d(TAG, "Foreground → $newPkg (Ignored)")
            return
        }

        Log.d(TAG, "Foreground → $newPkg | protection=${ProtectionState.isProtectionActive} | monitored=${ProtectionState.rules.keys}")

        // 3. If ProtectionState is empty, self-heal from DataStore
        ensureStateLoaded()

        // 4. Hard-lock check (instant lock or limit already reached)
        if (ProtectionState.isHardLocked(newPkg)) {
            val endTime = ProtectionState.getLockEndTime(newPkg)
            Log.d(TAG, "$newPkg is hard-locked until $endTime")
            showBlocker(newPkg, endTime)
            return
        }

        // Also check Room DB for active session (handles app restarts)
        val activeSession = lockSessionRepository.getActiveSessionSync()
        if (activeSession != null) {
            val remaining = activeSession.endTime - System.currentTimeMillis()
            if (remaining > 0 && activeSession.blockedApps.contains(newPkg)) {
                // Sync in-memory state from DB
                ProtectionState.hardLock(newPkg, activeSession.endTime)
                showBlocker(newPkg, activeSession.endTime)
                return
            } else if (remaining <= 0) {
                lockSessionRepository.completeSession(activeSession)
            }
        }

        // 5. Usage-based protection check
        if (!ProtectionState.isProtectionActive) {
            Log.d(TAG, "Protection not active, skipping $newPkg")
            return
        }
        val appRule = ProtectionState.rules[newPkg]
        if (appRule == null) {
            Log.d(TAG, "$newPkg is not monitored")
            return
        }

        val usageLimitMs = appRule.usageLimitMinutes * 60 * 1000L
        val lockoutMs = appRule.lockoutDurationMinutes * 60 * 1000L

        Log.d(TAG, "Monitoring $newPkg (usage so far: ${ProtectionState.getUsage(newPkg) / 1000}s, limit: ${usageLimitMs / 1000}s)")

        // 6. Start 500ms tick loop to accumulate usage
        trackingJob = scope.launch {
            while (isActive) {
                delay(500)
                val currentPkg = trackedPackage   // re-read volatile field
                if (currentPkg != newPkg) break    // app changed — stop tracking

                val elapsed    = System.currentTimeMillis() - trackStartTime
                val totalUsage = ProtectionState.getUsage(newPkg) + elapsed

                Log.d(TAG, "$newPkg usage: ${totalUsage / 1000}s / ${usageLimitMs / 1000}s")

                if (totalUsage >= usageLimitMs) {
                    // LIMIT REACHED
                    val lockoutEnd = System.currentTimeMillis() + lockoutMs

                    ProtectionState.hardLock(newPkg, lockoutEnd)
                    ProtectionState.resetUsage(newPkg)

                    // Persist in Room DB for crash recovery
                    lockSessionRepository.startSession(lockoutMs, listOf(newPkg))

                    Log.d(TAG, "LIMIT REACHED for $newPkg — locking until $lockoutEnd")
                    showBlocker(newPkg, lockoutEnd)
                    break
                }
            }
        }
    }

    private suspend fun ensureStateLoaded() {
        // Only reload if protection packages are empty (i.e. never loaded or cleared)
        if (ProtectionState.rules.isNotEmpty()) return

        val rules    = dataStore.getAppRulesSync()
        val isEnabled  = dataStore.isProtectionEnabledSync()

        ProtectionState.rules  = rules
        ProtectionState.isProtectionActive = isEnabled && rules.isNotEmpty()

        // Sync hard locks from DB
        val activeSession = lockSessionRepository.getActiveSessionSync()
        if (activeSession != null) {
            val remaining = activeSession.endTime - System.currentTimeMillis()
            if (remaining > 0) {
                activeSession.blockedApps.forEach { pkg ->
                    ProtectionState.hardLock(pkg, activeSession.endTime)
                }
            } else {
                lockSessionRepository.completeSession(activeSession)
            }
        }

        Log.d(TAG, "State self-healed: active=${ProtectionState.isProtectionActive}, apps=${rules.keys}")
    }

    private fun showBlocker(blockedPkg: String, endTime: Long) {
        val now = System.currentTimeMillis()

        // Debounce only if the blocking screen is actively showing for this exact package
        if (ProtectionState.isBlockingVisible && ProtectionState.currentBlockedPackage == blockedPkg) return

        // Debounce very fast duplicate events (e.g. 300ms)
        if (blockedPkg == lastLaunchPkg && now - lastLaunchTime < 300L) return

        lastLaunchPkg  = blockedPkg
        lastLaunchTime = now

        val intent = Intent(this, BlockingActivity::class.java).apply {
            putExtra(BlockingActivity.EXTRA_BLOCKED_PACKAGE, blockedPkg)
            putExtra(BlockingActivity.EXTRA_END_TIME, endTime)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
        }
        startActivity(intent)
    }

    private fun isIgnored(pkg: String): Boolean {
        val ownPkg = applicationContext.packageName
        return pkg == ownPkg ||
               pkg == "com.android.systemui" ||
               pkg == "android" ||
               pkg.contains("launcher", ignoreCase = true) ||
               pkg == "com.android.settings"
    }
}
