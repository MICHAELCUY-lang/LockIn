package com.example.lockin.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "lock_sessions")
data class LockSessionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val startTime: Long,
    val endTime: Long,
    val duration: Long,
    val status: String,
    val createdAt: Long,
    val blockedApps: List<String>
)
