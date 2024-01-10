package com.juanarton.chargingcurrentcontroller.batterymonitorservice

import android.app.AlarmManager
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.IBinder
import android.os.SystemClock
import android.util.Log
import androidx.core.app.NotificationCompat
import com.juanarton.chargingcurrentcontroller.R
import com.juanarton.core.data.domain.model.BatteryInfo
import com.juanarton.core.data.domain.repository.DataRepositoryInterface
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class BatteryMonitorService : Service() {

    private var isServiceStarted = false

    @Inject
    lateinit var dataRepository: DataRepositoryInterface
    private lateinit var notificationManager:NotificationManager
    private lateinit var builder: NotificationCompat.Builder
    private lateinit var screenStateReceiver: BroadcastReceiver

    companion object {
        const val NOTIFICATION_ID = 1
        const val CHANNEL_ID = "BatteryMonitorChannel"
        var isRegistered = false
        var delayDuration: Long = 5
        var serviceJob: Job? = null
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
        applicationContext.unregisterReceiver(screenStateReceiver)
        val restartServiceIntent = Intent(applicationContext, BatteryMonitorService::class.java).also {
            it.setPackage(packageName)
        }

        val restartServicePendingIntent: PendingIntent = PendingIntent.getService(this, 1, restartServiceIntent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE);
        applicationContext.getSystemService(Context.ALARM_SERVICE);
        val alarmService: AlarmManager = applicationContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager;
        alarmService.set(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() + 1000, restartServicePendingIntent)
    }

    override fun onDestroy() {
        super.onDestroy()

        applicationContext.unregisterReceiver(screenStateReceiver)
    }

    fun startMonitoring() {
        serviceJob = monitorBattery()
    }

    fun stopMonitoring() {
        serviceJob?.cancel()
    }

    private fun monitorBattery(): Job {
        return CoroutineScope(Dispatchers.IO).launch {
            while (isActive) {
                launch(Dispatchers.IO) {
                    dataRepository.getBatteryInfo().collect {
                        updateNotification(it)
                        Log.d("test", ((SystemClock.elapsedRealtime()-SystemClock.uptimeMillis())/1000).toString())
                    }
                }
                delay(delayDuration * 1000)
            }
        }
    }

    private fun startService() {
        if (!isServiceStarted) {
            isServiceStarted = true

            val intentFilter = IntentFilter()
            intentFilter.addAction(Intent.ACTION_SCREEN_ON)
            intentFilter.addAction(Intent.ACTION_SCREEN_OFF)
            screenStateReceiver = ScreenStateReceiver()
            applicationContext.registerReceiver(screenStateReceiver, intentFilter)

            isRegistered = true

            setServiceState(this, ServiceState.STARTED)
            startForeground(NOTIFICATION_ID, createNotification())

            startMonitoring()
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

    inner class ScreenStateReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent?) {
            Log.d("test", "work")
            when (intent?.action) {
                Intent.ACTION_SCREEN_ON -> {
                    stopMonitoring()
                    delayDuration = 5
                    startMonitoring()
                }
                Intent.ACTION_USER_PRESENT -> {
                    stopMonitoring()
                    delayDuration = 5
                    startMonitoring()
                }
                Intent.ACTION_SCREEN_OFF -> {
                    Log.d("test", "work")
                    stopMonitoring()
                    delayDuration = 30
                    startMonitoring()
                }
            }
        }
    }
}