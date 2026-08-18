package com.example.lockin.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.example.lockin.domain.model.SessionStatus
import com.example.lockin.domain.repository.LockSessionRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class AppMonitoringService : Service() {

    @Inject
    lateinit var lockSessionRepository: LockSessionRepository

    private val job = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.IO + job)
    private var tickerJob: Job? = null

    companion object {
        private const val CHANNEL_ID = "lockin_monitoring"
        private const val NOTIFICATION_ID = 1
        
        fun start(context: Context) {
            try {
                val intent = Intent(context, AppMonitoringService::class.java)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    context.startForegroundService(intent)
                } else {
                    context.startService(intent)
                }
            } catch (_: Exception) {}
        }
        
        fun stop(context: Context) {
            try {
                val intent = Intent(context, AppMonitoringService::class.java)
                context.stopService(intent)
            } catch (_: Exception) {}
        }
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        try {
            startForeground(NOTIFICATION_ID, createNotification("LockIn active..."))
        } catch (_: Exception) {}
        
        scope.launch {
            lockSessionRepository.getActiveSession().collectLatest { session ->
                tickerJob?.cancel()
                if (session != null && session.status == SessionStatus.ACTIVE) {
                    tickerJob = launch {
                        while (isActive) {
                            val remaining = session.endTime - System.currentTimeMillis()
                            if (remaining <= 0) {
                                lockSessionRepository.completeSession(session)
                                stopSelf()
                                break
                            }
                            
                            val minutes = (remaining / 1000) / 60
                            val seconds = (remaining / 1000) % 60
                            val text = String.format("%02d:%02d remaining", minutes, seconds)
                            updateNotification(text)
                            
                            delay(1000)
                        }
                    }
                } else {
                    stopSelf()
                }
            }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        tickerJob?.cancel()
        scope.cancel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "LockIn Monitoring",
                NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    private fun createNotification(text: String): android.app.Notification {
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("LockIn Active")
            .setContentText(text)
            .setSmallIcon(android.R.drawable.ic_lock_lock)
            .setOngoing(true)
            .build()
    }

    private fun updateNotification(text: String) {
        try {
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.notify(NOTIFICATION_ID, createNotification(text))
        } catch (_: Exception) {}
    }
}
