package com.example.lockin.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.example.lockin.domain.model.AppRule
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class DataStoreManager(private val context: Context) {

    companion object {
        val ONBOARDING_COMPLETED = booleanPreferencesKey("onboarding_completed")
        // Storing AppRule serialized strings
        val APP_RULES = stringSetPreferencesKey("app_rules_set")
        val IS_PROTECTION_ENABLED = booleanPreferencesKey("is_protection_enabled")
    }

    val onboardingCompleted: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[ONBOARDING_COMPLETED] ?: false
    }

    val appRules: Flow<Map<String, AppRule>> = context.dataStore.data.map { preferences ->
        val rawSet = preferences[APP_RULES] ?: emptySet()
        rawSet.mapNotNull { AppRule.fromSerializedString(it) }
            .associateBy { it.packageName }
    }

    val isProtectionEnabled: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[IS_PROTECTION_ENABLED] ?: true
    }

    suspend fun setOnboardingCompleted(completed: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[ONBOARDING_COMPLETED] = completed
        }
    }

    suspend fun setAppRules(rules: Map<String, AppRule>) {
        context.dataStore.edit { preferences ->
            preferences[APP_RULES] = rules.values.map { it.toSerializedString() }.toSet()
        }
    }

    suspend fun setProtectionEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[IS_PROTECTION_ENABLED] = enabled
        }
    }

    suspend fun getAppRulesSync(): Map<String, AppRule> {
        val rawSet = context.dataStore.data.first()[APP_RULES] ?: emptySet()
        return rawSet.mapNotNull { AppRule.fromSerializedString(it) }
            .associateBy { it.packageName }
    }

    suspend fun isProtectionEnabledSync(): Boolean {
        return context.dataStore.data.first()[IS_PROTECTION_ENABLED] ?: true
    }
}
