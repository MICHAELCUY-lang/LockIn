package com.example.lockin.domain.model

data class LockSession(
    val id: Int = 0,
    val startTime: Long,
    val endTime: Long,
    val duration: Long,
    val status: SessionStatus,
    val createdAt: Long,
    val blockedApps: List<String>
)

enum class SessionStatus {
    ACTIVE,
    COMPLETED
}
