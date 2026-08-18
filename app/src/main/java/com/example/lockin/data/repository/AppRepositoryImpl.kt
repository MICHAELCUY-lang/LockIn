package com.example.lockin.data.repository

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import com.example.lockin.domain.model.AppInfo
import com.example.lockin.domain.repository.AppRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AppRepositoryImpl(
    private val context: Context
) : AppRepository {

    override suspend fun getInstalledApps(): List<AppInfo> = withContext(Dispatchers.IO) {
        val packageManager = context.packageManager
        val intent = Intent(Intent.ACTION_MAIN, null).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
        }
        val resolveInfos = packageManager.queryIntentActivities(intent, 0)
        
        resolveInfos.mapNotNull { resolveInfo ->
            val packageName = resolveInfo.activityInfo.packageName
            if (packageName == context.packageName) return@mapNotNull null // Exclude self
            
            val appName = resolveInfo.loadLabel(packageManager).toString()
            val icon = resolveInfo.loadIcon(packageManager)
            
            AppInfo(
                packageName = packageName,
                name = appName,
                icon = icon
            )
        }.sortedBy { it.name.lowercase() }
    }
}
