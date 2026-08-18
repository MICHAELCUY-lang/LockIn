package com.example.lockin

import android.app.Application
import com.example.lockin.data.local.DataStoreManager
import com.example.lockin.service.ProtectionState
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltAndroidApp
class LockInApplication : Application() {

    // Hilt can inject into Application if needed, but for DataStoreManager
    // we'll create it directly since Hilt hasn't finished init yet at onCreate.
    private val appScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()
        // Eagerly restore ProtectionState from DataStore at app start,
        // so the AccessibilityService has correct data immediately.
        restoreProtectionStateEager()
    }

    private fun restoreProtectionStateEager() {
        val dsManager = DataStoreManager(this)
        appScope.launch {
            val rules = dsManager.getAppRulesSync()
            val isEnabled = dsManager.isProtectionEnabledSync()

            ProtectionState.rules = rules
            ProtectionState.isProtectionActive = isEnabled && rules.isNotEmpty()

            android.util.Log.d("LockIn", "ProtectionState restored: active=${ProtectionState.isProtectionActive}, apps=${rules.keys}")
        }
    }
}
