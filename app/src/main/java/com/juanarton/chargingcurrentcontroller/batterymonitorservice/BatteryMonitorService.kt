package com.juanarton.chargingcurrentcontroller.batterymonitorservice

import android.app.AlarmManager
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.os.PowerManager
import android.os.SystemClock
import android.util.Log
import androidx.core.app.NotificationCompat
import com.juanarton.chargingcurrentcontroller.R
import com.juanarton.core.data.domain.model.BatteryInfo
import com.juanarton.core.data.domain.repository.DataRepositoryInterface
import com.juanarton.core.data.repository.DataRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class BatteryMonitorService : Service() {

    private var isServiceStarted = false

    @Inject
    lateinit var dataRepository: DataRepositoryInterface
    private lateinit var notificationManager:NotificationManager
    private lateinit var builder: NotificationCompat.Builder

    companion object {
        const val NOTIFICATION_ID = 1
        const val CHANNEL_ID = "BatteryMonitorChannel"
    }
    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        if (intent != null) {
            when (intent.action) {
                Action.START.name -> startService()
                Action.STOP.name -> stopService()
                else -> Log.d("BatteryMonitorService", "No action in received intent")
            }
        } else {
            Log.d("BatteryMonitorService", "Null intent")
        }

        return START_STICKY
    }

    override fun onTaskRemoved(rootIntent: Intent) {
        val restartServiceIntent = Intent(applicationContext, BatteryMonitorService::class.java).also {
            it.setPackage(packageName)
        }

        val restartServicePendingIntent: PendingIntent = PendingIntent.getService(this, 1, restartServiceIntent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE);
        applicationContext.getSystemService(Context.ALARM_SERVICE);
        val alarmService: AlarmManager = applicationContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager;
        alarmService.set(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() + 1000, restartServicePendingIntent)
    }

    private fun startService() {
        if (!isServiceStarted) {
            isServiceStarted = true

            setServiceState(this, ServiceState.STARTED)

            startForeground(NOTIFICATION_ID, createNotification())

            CoroutineScope(Dispatchers.IO).launch {
                while (isServiceStarted) {
                    launch(Dispatchers.IO) {
                        dataRepository.getBatteryInfo().collect {
                            updateNotification(it)
                        }
                    }
                    delay(5 * 1000)
                }
            }
        }
    }

    private fun stopService() {
        try {
            stopForeground(STOP_FOREGROUND_REMOVE)
            stopSelf()
        } catch (e: Exception) {
            Log.d("BatteryMonitorService", "Service stopped without being started: $e")
        }
        isServiceStarted = false
        setServiceState(this, ServiceState.STOPPED)
    }

    private fun createNotification(): Notification {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "3C Battery Monitor"
            val description = "Battery Monitor Service Channel"
            val importance = NotificationManager.IMPORTANCE_LOW
            val channel = NotificationChannel(CHANNEL_ID, name, importance)
            channel.description = description

            notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            notificationManager.createNotificationChannel(channel)
        }

        builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.power)
            .setContentTitle(getString(R.string.serviceNotificationTitle))
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText("Test")
            )
            .setShowWhen(false)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        return builder.build()
    }

    private fun updateNotification(batteryInfo: BatteryInfo) {
        val title = buildString {
            append("${batteryInfo.level}%")
            append("  ·  ")
            append("${batteryInfo.temperature}${getString(R.string.degree_symbol)}")
            append("  ·  ")
            append("${batteryInfo.currentNow} ${getString(R.string.ma)}")
        }

        builder.setContentTitle(title)
        notificationManager.notify(NOTIFICATION_ID, builder.build())
    }
}