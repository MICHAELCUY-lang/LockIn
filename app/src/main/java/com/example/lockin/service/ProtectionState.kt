package com.example.lockin.service

/**
 * Singleton in-memory state shared between the UI and LockAccessibilityService.
 * This avoids the race condition of reading from DataStore/DB asynchronously
 * inside the AccessibilityService.
 */
object ProtectionState {

    // Whether the BlockingActivity is currently visible
    @Volatile
    var isBlockingVisible: Boolean = false

    @Volatile
    var currentBlockedPackage: String = ""

    // ---- Configuration set by UI before activating ----

    @Volatile
    var isProtectionActive: Boolean = false

    @Volatile
    var monitoredPackages: Set<String> = emptySet()

    // Usage limit in milliseconds
    @Volatile
    var usageLimitMs: Long = 30 * 60 * 1000L // 30 min default

    // Lockout duration in milliseconds
    @Volatile
    var lockoutDurationMs: Long = 30 * 60 * 1000L // 30 min default

    // ---- In-memory usage accumulator per package (ms) ----
    private val _usageMap = HashMap<String, Long>()
    private val lock = Any()

    fun addUsage(packageName: String, ms: Long) {
        synchronized(lock) {
            _usageMap[packageName] = (_usageMap[packageName] ?: 0L) + ms
        }
    }

    fun getUsage(packageName: String): Long {
        return synchronized(lock) { _usageMap[packageName] ?: 0L }
    }

    fun resetUsage(packageName: String) {
        synchronized(lock) { _usageMap[packageName] = 0L }
    }

    // ---- Hard locked apps (instant lock or limit triggered) ----
    // packageName -> endTime in epoch ms
    private val _hardLockedMap = HashMap<String, Long>()

    fun hardLock(packageName: String, endTime: Long) {
        synchronized(lock) { _hardLockedMap[packageName] = endTime }
    }

    fun isHardLocked(packageName: String): Boolean {
        val now = System.currentTimeMillis()
        return synchronized(lock) {
            val endTime = _hardLockedMap[packageName] ?: return false
            if (now < endTime) {
                true
            } else {
                _hardLockedMap.remove(packageName)
                false
            }
        }
    }

    fun getLockEndTime(packageName: String): Long {
        return synchronized(lock) { _hardLockedMap[packageName] ?: 0L }
    }

    fun clearHardLock(packageName: String) {
        synchronized(lock) { _hardLockedMap.remove(packageName) }
    }

    fun clearAllLocks() {
        synchronized(lock) { _hardLockedMap.clear() }
    }
}
