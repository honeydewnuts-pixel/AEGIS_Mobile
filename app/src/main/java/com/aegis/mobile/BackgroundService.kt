package com.aegis.mobile

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat

class BackgroundService : Service() {

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForeground(1, createNotification())
        // TODO v1.1: Add MediaProjection + Screenshot logic here
        // TODO v1.1: Send screenshot bitmap to BrainAnalyzer.kt
        return START_STICKY
    }

    private fun createNotification(): Notification {
        val channel = NotificationChannel("aegis", "AEGIS Service", NotificationManager.IMPORTANCE_LOW)
        getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
        return NotificationCompat.Builder(this, "aegis")
            .setContentTitle("AEGIS Running")
            .setContentText("Monitoring MT5 in background")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .build()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
