package com.example.lockin.domain.repository

import com.example.lockin.domain.model.LockSession
import kotlinx.coroutines.flow.Flow

interface LockSessionRepository {
    fun getActiveSession(): Flow<LockSession?>
    suspend fun getActiveSessionSync(): LockSession?
    fun getAllSessions(): Flow<List<LockSession>>
    suspend fun startSession(durationMs: Long, blockedApps: List<String>)
    suspend fun completeSession(session: LockSession)
}
