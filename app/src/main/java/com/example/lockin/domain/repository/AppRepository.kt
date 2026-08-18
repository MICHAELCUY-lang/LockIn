package com.example.lockin.domain.repository

import com.example.lockin.domain.model.AppInfo

interface AppRepository {
    suspend fun getInstalledApps(): List<AppInfo>
}
