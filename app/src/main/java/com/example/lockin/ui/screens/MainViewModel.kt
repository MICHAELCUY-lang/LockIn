package com.example.lockin.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lockin.data.local.DataStoreManager
import com.example.lockin.domain.model.AppInfo
import com.example.lockin.domain.repository.AppRepository
import com.example.lockin.domain.repository.LockSessionRepository
import com.example.lockin.service.ProtectionState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val appRepository: AppRepository,
    private val lockSessionRepository: LockSessionRepository,
    private val dataStoreManager: DataStoreManager
) : ViewModel() {

    private val _installedApps = MutableStateFlow<List<AppInfo>>(emptyList())
    val installedApps: StateFlow<List<AppInfo>> = _installedApps.asStateFlow()

    private val _selectedApps = MutableStateFlow<Set<String>>(emptySet())
    val selectedApps: StateFlow<Set<String>> = _selectedApps.asStateFlow()

    val monitoredApps: Flow<Set<String>> = dataStoreManager.monitoredApps
    val usageLimitMinutes: Flow<Long> = dataStoreManager.usageLimitMinutes
    val lockoutDurationMinutes: Flow<Long> = dataStoreManager.lockoutDurationMinutes
    val isProtectionEnabled: Flow<Boolean> = dataStoreManager.isProtectionEnabled

    val activeSession = lockSessionRepository.getActiveSession()
    val allSessions = lockSessionRepository.getAllSessions()

    init {
        loadApps()
        loadSavedMonitoredApps()
        restoreProtectionState()
    }

    private fun loadApps() {
        viewModelScope.launch {
            _installedApps.value = appRepository.getInstalledApps()
        }
    }

    private fun loadSavedMonitoredApps() {
        viewModelScope.launch {
            val saved = dataStoreManager.getMonitoredAppsSync()
            if (saved.isNotEmpty()) {
                _selectedApps.value = saved
            }
        }
    }

    /**
     * On app start, restore ProtectionState from persisted DataStore settings
     * so the AccessibilityService can function immediately after reboot/restart.
     */
    private fun restoreProtectionState() {
        viewModelScope.launch {
            val monApps      = dataStoreManager.getMonitoredAppsSync()
            val usageLimit   = dataStoreManager.getUsageLimitMinutesSync()
            val lockoutDur   = dataStoreManager.getLockoutDurationMinutesSync()

            // isProtectionEnabled default is true in DataStore, but we only
            // treat protection as active if apps are actually configured.
            val isActive = monApps.isNotEmpty()

            ProtectionState.monitoredPackages  = monApps
            ProtectionState.usageLimitMs       = usageLimit * 60 * 1000L
            ProtectionState.lockoutDurationMs  = lockoutDur * 60 * 1000L
            ProtectionState.isProtectionActive = isActive

            android.util.Log.d("LockIn", "ViewModel restored ProtectionState: active=$isActive, apps=$monApps, limit=${usageLimit}m")
        }
    }

    fun toggleAppSelection(packageName: String) {
        _selectedApps.update { current ->
            if (current.contains(packageName)) current - packageName
            else current + packageName
        }
    }

    fun clearSelection() {
        _selectedApps.value = emptySet()
    }

    /**
     * Save protection config to DataStore AND immediately apply to ProtectionState
     * so the service responds instantly without async delays.
     */
    fun saveProtectionConfig(usageLimitMinutes: Long, lockoutDurationMinutes: Long) {
        val apps       = _selectedApps.value
        val usageLimitMs = usageLimitMinutes * 60 * 1000L
        val lockoutMs    = lockoutDurationMinutes * 60 * 1000L

        // 1. Immediately push to in-memory singleton (service reads this synchronously)
        ProtectionState.monitoredPackages  = apps
        ProtectionState.usageLimitMs       = usageLimitMs
        ProtectionState.lockoutDurationMs  = lockoutMs
        ProtectionState.isProtectionActive = apps.isNotEmpty()

        android.util.Log.d("LockIn", "saveProtectionConfig: apps=$apps, limit=${usageLimitMinutes}m, lockout=${lockoutDurationMinutes}m, active=${apps.isNotEmpty()}")

        // 2. Persist to DataStore (async, for recovery after restarts)
        viewModelScope.launch {
            dataStoreManager.setMonitoredApps(apps)
            dataStoreManager.setUsageLimitMinutes(usageLimitMinutes)
            dataStoreManager.setLockoutDurationMinutes(lockoutDurationMinutes)
            dataStoreManager.setProtectionEnabled(apps.isNotEmpty())
            android.util.Log.d("LockIn", "DataStore saved: apps=$apps")
        }
    }

    /**
     * Instantly lock selected apps for the given duration.
     * This writes to Room DB AND immediately applies the hard lock in ProtectionState.
     */
    fun startInstantLockSession(lockoutDurationMinutes: Long) {
        val apps = _selectedApps.value.toList()
        val lockoutMs = lockoutDurationMinutes * 60 * 1000L
        val endTime = System.currentTimeMillis() + lockoutMs

        // Immediately hard-lock in memory (service will enforce on very next window change)
        apps.forEach { pkg ->
            ProtectionState.hardLock(pkg, endTime)
        }

        // Also persist to Room DB for crash recovery
        viewModelScope.launch {
            lockSessionRepository.startSession(lockoutMs, apps)
        }
    }
}
