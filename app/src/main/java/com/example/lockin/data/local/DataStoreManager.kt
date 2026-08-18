package com.example.lockin.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class DataStoreManager(private val context: Context) {

    companion object {
        val ONBOARDING_COMPLETED = booleanPreferencesKey("onboarding_completed")
        val MONITORED_APPS = stringSetPreferencesKey("monitored_apps")
        val USAGE_LIMIT_MINUTES = longPreferencesKey("usage_limit_minutes")
        val LOCKOUT_DURATION_MINUTES = longPreferencesKey("lockout_duration_minutes")
        val IS_PROTECTION_ENABLED = booleanPreferencesKey("is_protection_enabled")
    }

    val onboardingCompleted: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[ONBOARDING_COMPLETED] ?: false
    }

    val monitoredApps: Flow<Set<String>> = context.dataStore.data.map { preferences ->
        preferences[MONITORED_APPS] ?: emptySet()
    }

    val usageLimitMinutes: Flow<Long> = context.dataStore.data.map { preferences ->
        preferences[USAGE_LIMIT_MINUTES] ?: 30L
    }

    val lockoutDurationMinutes: Flow<Long> = context.dataStore.data.map { preferences ->
        preferences[LOCKOUT_DURATION_MINUTES] ?: 30L
    }

    val isProtectionEnabled: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[IS_PROTECTION_ENABLED] ?: true
    }

    suspend fun setOnboardingCompleted(completed: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[ONBOARDING_COMPLETED] = completed
        }
    }

    suspend fun setMonitoredApps(apps: Set<String>) {
        context.dataStore.edit { preferences ->
            preferences[MONITORED_APPS] = apps
        }
    }

    suspend fun setUsageLimitMinutes(minutes: Long) {
        context.dataStore.edit { preferences ->
            preferences[USAGE_LIMIT_MINUTES] = minutes
        }
    }

    suspend fun setLockoutDurationMinutes(minutes: Long) {
        context.dataStore.edit { preferences ->
            preferences[LOCKOUT_DURATION_MINUTES] = minutes
        }
    }

    suspend fun setProtectionEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[IS_PROTECTION_ENABLED] = enabled
        }
    }

    suspend fun getMonitoredAppsSync(): Set<String> {
        return context.dataStore.data.first()[MONITORED_APPS] ?: emptySet()
    }

    suspend fun getUsageLimitMinutesSync(): Long {
        return context.dataStore.data.first()[USAGE_LIMIT_MINUTES] ?: 30L
    }

    suspend fun getLockoutDurationMinutesSync(): Long {
        return context.dataStore.data.first()[LOCKOUT_DURATION_MINUTES] ?: 30L
    }

    suspend fun isProtectionEnabledSync(): Boolean {
        return context.dataStore.data.first()[IS_PROTECTION_ENABLED] ?: true
    }
}
