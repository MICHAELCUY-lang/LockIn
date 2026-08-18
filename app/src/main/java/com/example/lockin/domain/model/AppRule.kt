package com.example.lockin.domain.model

/**
 * Defines the protection rule for a specific app.
 * @param packageName The package name of the app (e.g. "com.instagram.android")
 * @param usageLimitMinutes How many minutes the app can be used before being locked
 * @param lockoutDurationMinutes How many minutes the app remains locked once the limit is reached
 */
data class AppRule(
    val packageName: String,
    val usageLimitMinutes: Long = 30L,
    val lockoutDurationMinutes: Long = 30L
) {
    // Helper to serialize to string
    fun toSerializedString(): String {
        return "$packageName|$usageLimitMinutes|$lockoutDurationMinutes"
    }

    companion object {
        // Helper to deserialize from string
        fun fromSerializedString(serialized: String): AppRule? {
            val parts = serialized.split("|")
            if (parts.size >= 3) {
                return try {
                    AppRule(
                        packageName = parts[0],
                        usageLimitMinutes = parts[1].toLong(),
                        lockoutDurationMinutes = parts[2].toLong()
                    )
                } catch (e: Exception) {
                    null
                }
            }
            // Fallback for old format which was just a package name
            return if (serialized.isNotBlank() && !serialized.contains("|")) {
                AppRule(packageName = serialized)
            } else null
        }
    }
}
