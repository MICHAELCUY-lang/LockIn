package com.example.lockin.di

import android.content.Context
import androidx.room.Room
import com.example.lockin.data.local.DataStoreManager
import com.example.lockin.data.local.LockInDatabase
import com.example.lockin.data.local.dao.LockSessionDao
import com.example.lockin.data.repository.AppRepositoryImpl
import com.example.lockin.data.repository.LockSessionRepositoryImpl
import com.example.lockin.domain.repository.AppRepository
import com.example.lockin.domain.repository.LockSessionRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): LockInDatabase {
        return Room.databaseBuilder(
            context,
            LockInDatabase::class.java,
            "lockin_db"
        ).build()
    }

    @Provides
    fun provideLockSessionDao(database: LockInDatabase): LockSessionDao {
        return database.lockSessionDao
    }

    @Provides
    @Singleton
    fun provideLockSessionRepository(dao: LockSessionDao): LockSessionRepository {
        return LockSessionRepositoryImpl(dao)
    }

    @Provides
    @Singleton
    fun provideAppRepository(@ApplicationContext context: Context): AppRepository {
        return AppRepositoryImpl(context)
    }

    @Provides
    @Singleton
    fun provideDataStoreManager(@ApplicationContext context: Context): DataStoreManager {
        return DataStoreManager(context)
    }
}
