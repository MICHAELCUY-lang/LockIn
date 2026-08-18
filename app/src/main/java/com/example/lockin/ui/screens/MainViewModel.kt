package com.example.lockin.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lockin.data.local.DataStoreManager
import com.example.lockin.domain.model.AppInfo
import com.example.lockin.domain.model.AppRule
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

    private val _appRules = MutableStateFlow<Map<String, AppRule>>(emptyMap())
    val appRules: StateFlow<Map<String, AppRule>> = _appRules.asStateFlow()

    // For backward compatibility / ease of use
    val selectedApps: StateFlow<Set<String>> = _appRules.map { it.keys }.stateIn(viewModelScope, SharingStarted.Lazily, emptySet())
    val monitoredApps: Flow<Set<String>> = dataStoreManager.appRules.map { it.keys }

    val isProtectionEnabled: Flow<Boolean> = dataStoreManager.isProtectionEnabled

    // The app currently being configured in LockSetupScreen
    private val _selectedAppToConfigure = MutableStateFlow<String?>(null)
    val selectedAppToConfigure: StateFlow<String?> = _selectedAppToConfigure.asStateFlow()

    val activeSession = lockSessionRepository.getActiveSession()
    val allSessions = lockSessionRepository.getAllSessions()

    init {
        loadApps()
        loadSavedAppRules()
        restoreProtectionState()
    }

    private fun loadApps() {
        viewModelScope.launch {
            _installedApps.value = appRepository.getInstalledApps()
        }
    }

    private fun loadSavedAppRules() {
        viewModelScope.launch {
            val saved = dataStoreManager.getAppRulesSync()
            if (saved.isNotEmpty()) {
                _appRules.value = saved
            }
        }
    }

    private fun restoreProtectionState() {
        viewModelScope.launch {
            val rules = dataStoreManager.getAppRulesSync()
            val isActive = rules.isNotEmpty()

            ProtectionState.rules = rules
            ProtectionState.isProtectionActive = isActive

            android.util.Log.d("LockIn", "ViewModel restored ProtectionState: active=$isActive, rules size=${rules.size}")
        }
    }

    fun selectAppToConfigure(pkg: String) {
        _selectedAppToConfigure.value = pkg
    }

    fun toggleAppSelection(packageName: String) {
        val currentRules = _appRules.value.toMutableMap()
        
        if (currentRules.containsKey(packageName)) {
            currentRules.remove(packageName)
        } else {
            currentRules[packageName] = AppRule(packageName, usageLimitMinutes = 30L, lockoutDurationMinutes = 30L)
        }
        
        _appRules.value = currentRules

        viewModelScope.launch {
            dataStoreManager.setAppRules(currentRules)
            ProtectionState.rules = currentRules
            
            val isActive = currentRules.isNotEmpty()
            ProtectionState.isProtectionActive = isActive
            dataStoreManager.setProtectionEnabled(isActive)
        }
    }

    fun clearSelection() {
        _appRules.value = emptyMap()
    }

    fun saveAppRule(packageName: String, usageLimitMinutes: Long, lockoutDurationMinutes: Long) {
        val currentRules = _appRules.value.toMutableMap()
        currentRules[packageName] = AppRule(packageName, usageLimitMinutes, lockoutDurationMinutes)
        
        _appRules.value = currentRules

        ProtectionState.rules = currentRules
        ProtectionState.isProtectionActive = currentRules.isNotEmpty()

        viewModelScope.launch {
            dataStoreManager.setAppRules(currentRules)
            dataStoreManager.setProtectionEnabled(currentRules.isNotEmpty())
        }
    }

    fun startInstantLockSessionForApp(packageName: String, lockoutDurationMinutes: Long) {
        val lockoutMs = lockoutDurationMinutes * 60 * 1000L
        val endTime = System.currentTimeMillis() + lockoutMs

        ProtectionState.hardLock(packageName, endTime)

        viewModelScope.launch {
            lockSessionRepository.startSession(lockoutMs, listOf(packageName))
        }
    }
}
