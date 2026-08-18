package com.example.lockin.data.repository

import com.example.lockin.data.local.dao.LockSessionDao
import com.example.lockin.data.local.entity.LockSessionEntity
import com.example.lockin.domain.model.LockSession
import com.example.lockin.domain.model.SessionStatus
import com.example.lockin.domain.repository.LockSessionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class LockSessionRepositoryImpl(
    private val dao: LockSessionDao
) : LockSessionRepository {

    override fun getActiveSession(): Flow<LockSession?> {
        return dao.getActiveSession().map { it?.toDomain() }
    }

    override suspend fun getActiveSessionSync(): LockSession? {
        return dao.getActiveSessionSync()?.toDomain()
    }

    override fun getAllSessions(): Flow<List<LockSession>> {
        return dao.getAllSessions().map { list -> list.map { it.toDomain() } }
    }

    override suspend fun startSession(durationMs: Long, blockedApps: List<String>) {
        val now = System.currentTimeMillis()
        val entity = LockSessionEntity(
            startTime = now,
            endTime = now + durationMs,
            duration = durationMs,
            status = SessionStatus.ACTIVE.name,
            createdAt = now,
            blockedApps = blockedApps
        )
        dao.insertSession(entity)
    }

    override suspend fun completeSession(session: LockSession) {
        val entity = session.toEntity().copy(status = SessionStatus.COMPLETED.name)
        dao.updateSession(entity)
    }

    private fun LockSessionEntity.toDomain(): LockSession {
        return LockSession(
            id = id,
            startTime = startTime,
            endTime = endTime,
            duration = duration,
            status = SessionStatus.valueOf(status),
            createdAt = createdAt,
            blockedApps = blockedApps
        )
    }

    private fun LockSession.toEntity(): LockSessionEntity {
        return LockSessionEntity(
            id = id,
            startTime = startTime,
            endTime = endTime,
            duration = duration,
            status = status.name,
            createdAt = createdAt,
            blockedApps = blockedApps
        )
    }
}
