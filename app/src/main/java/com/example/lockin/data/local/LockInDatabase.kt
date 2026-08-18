package com.example.lockin.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.lockin.data.local.dao.LockSessionDao
import com.example.lockin.data.local.entity.LockSessionEntity

@Database(entities = [LockSessionEntity::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class LockInDatabase : RoomDatabase() {
    abstract val lockSessionDao: LockSessionDao
}
