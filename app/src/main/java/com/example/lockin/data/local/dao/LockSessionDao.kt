package com.example.lockin.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.lockin.data.local.entity.LockSessionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LockSessionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: LockSessionEntity): Long

    @Update
    suspend fun updateSession(session: LockSessionEntity)

    @Query("SELECT * FROM lock_sessions WHERE status = 'ACTIVE' ORDER BY createdAt DESC LIMIT 1")
    fun getActiveSession(): Flow<LockSessionEntity?>

    @Query("SELECT * FROM lock_sessions WHERE status = 'ACTIVE' ORDER BY createdAt DESC LIMIT 1")
    suspend fun getActiveSessionSync(): LockSessionEntity?

    @Query("SELECT * FROM lock_sessions ORDER BY createdAt DESC")
    fun getAllSessions(): Flow<List<LockSessionEntity>>
}
