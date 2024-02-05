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
import com.juanarton.chargingcurrentcontroller.broadcastreceiver.BatteryStateReceiver
import com.juanarton.chargingcurrentcontroller.broadcastreceiver.PowerStateReceiver
import com.juanarton.chargingcurrentcontroller.utils.BatteryDataHolder.addScreenOffDrainPerHr
import com.juanarton.chargingcurrentcontroller.utils.BatteryDataHolder.addScreenOffTime
import com.juanarton.chargingcurrentcontroller.utils.BatteryDataHolder.addScreenOnDrainPerHr
import com.juanarton.chargingcurrentcontroller.utils.BatteryDataHolder.addScreenOnTime
import com.juanarton.chargingcurrentcontroller.utils.BatteryDataHolder.getScreenOffTime
import com.juanarton.chargingcurrentcontroller.utils.BatteryDataHolder.getScreenOnDrainPerHr
import com.juanarton.chargingcurrentcontroller.utils.BatteryDataHolder.getScreenOnTime
import com.juanarton.chargingcurrentcontroller.utils.ServiceUtil
import com.juanarton.core.data.domain.batteryInfo.model.BatteryInfo
import com.juanarton.core.data.domain.batteryInfo.repository.BatteryInfoRepositoryInterface
import com.juanarton.core.data.domain.batteryMonitoring.repository.BatteryMonitoringRepoInterface
import com.juanarton.core.utils.BatteryUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@AndroidEntryPoint
class BatteryMonitorService : Service() {

    private var isServiceStarted = false
    @Inject
    lateinit var batteryInfoRepositoryInterface: BatteryInfoRepositoryInterface
    @Inject
    lateinit var batteryMonitoringRepoInterface: BatteryMonitoringRepoInterface
    private lateinit var notificationManager:NotificationManager
    private lateinit var builder: NotificationCompat.Builder
    private val screenStateReceiver = ScreenStateReceiver()
    private val powerStateReceiver = PowerStateReceiver()
    private val batteryStateReceiver = BatteryStateReceiver()

    companion object {
        const val SERVICE_NOTIFICATION_ID = 1
        const val SERVICE_NOTIF_CHANNEL_ID = "BatteryMonitorChannel"
        var isRegistered = false
        var delayDuration: Long = 5
        var serviceJob: Job? = null
        var deepSleepInitialValue: Long = 0
        var screenOnBuffer: Long = 0
        var screenOffBuffer: Long = 0
        var deepSleep: Long = 0
        var deepSleepBuffer: Long = 0
        var cpuAwake: Long = 0
        var screenOn = true
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
        unregisterReceiver()
        val restartServiceIntent = Intent(applicationContext, BatteryMonitorService::class.java).also {
            it.setPackage(packageName)
        }

        val restartServicePendingIntent: PendingIntent = PendingIntent.getService(this, 1, restartServiceIntent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE)
        applicationContext.getSystemService(Context.ALARM_SERVICE)
        val alarmService: AlarmManager = applicationContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmService.set(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() + 1000, restartServicePendingIntent)
        registerReceiver()
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver()
    }

    override fun onCreate() {
        super.onCreate()

        registerReceiver()
    }

    private fun monitorBattery(): Job {
        return CoroutineScope(Dispatchers.IO).launch {
            while (isActive) {
                addScreenOnTime(batteryMonitoringRepoInterface.getScreenOnTime())
                addScreenOffTime(batteryMonitoringRepoInterface.getScreenOffTime())
                val screenOnDrain = batteryMonitoringRepoInterface.getScreenOnDrain()
                val screenOffDrain = batteryMonitoringRepoInterface.getScreenOffDrain()
                batteryInfoRepositoryInterface.getBatteryInfo().collect {
                    updateNotification(
                        it, getScreenOnTime(), getScreenOffTime(), getScreenOnDrainPerHr(),
                        getScreenOnDrainPerHr(), screenOnDrain, screenOffDrain
                    )
                    when (screenOn) {
                        true -> {
                            insertScreenOnTime()
                            addScreenOnDrainPerHr(screenOnDrain / (getScreenOnTime().toDouble()/3600))
                            addScreenOffDrainPerHr(screenOffDrain / (getScreenOffTime().toDouble()/3600))
                        }
                        false -> {
                            insertScreenOffTime()
                        }
                    }
                }
                delay(delayDuration * 1000)
            }
        }
    }

    private fun startMonitoring() {
        serviceJob = monitorBattery()
    }

    private fun startService() {
        if (!isServiceStarted) {
            isServiceStarted = true

            isRegistered = true

            deepSleepInitialValue =
                (SystemClock.elapsedRealtime() - SystemClock.uptimeMillis()) / 1000
            deepSleep = batteryMonitoringRepoInterface.getDeepSleepInitialValue()
            deepSleepBuffer = batteryMonitoringRepoInterface.getDeepSleepInitialValue()
            screenOnBuffer = batteryMonitoringRepoInterface.getScreenOnTime()
            screenOffBuffer = batteryMonitoringRepoInterface.getScreenOffTime()
            cpuAwake = batteryMonitoringRepoInterface.getCpuAwake()
            batteryMonitoringRepoInterface.insertStartTime(Date())

            setServiceState(this, ServiceState.STARTED)
            startForeground(SERVICE_NOTIFICATION_ID, createNotification())

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
            val channel = NotificationChannel(SERVICE_NOTIF_CHANNEL_ID, name, NotificationManager.IMPORTANCE_MIN)
            channel.description = description
            notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

        builder = NotificationCompat.Builder(this, SERVICE_NOTIF_CHANNEL_ID)
            .setForegroundServiceBehavior(NotificationCompat.FOREGROUND_SERVICE_IMMEDIATE)
            .setSmallIcon(R.drawable.power)
            .setContentTitle(getString(R.string.serviceNotificationTitle))
            .setShowWhen(false)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        return builder.build()
    }

    private fun updateNotification(
        batteryInfo: BatteryInfo, screenOnTime: Long, screenOffTime: Long, screenOnDrainPerHr: Double,
        screenOffDrainPerHr: Double, screenOnDrain:Int, screenOffDrain: Int
    ) {
        val title = ServiceUtil.buildTitle(batteryInfo, applicationContext)
        val content = ServiceUtil.buildContent(
            batteryInfo, deepSleep, screenOnTime, screenOffTime, cpuAwake, screenOnDrainPerHr,
            screenOffDrainPerHr, screenOnDrain, screenOffDrain
        )

        builder.setContentTitle(title)
            .setForegroundServiceBehavior(NotificationCompat.FOREGROUND_SERVICE_IMMEDIATE)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(content)
            )
        notificationManager.notify(SERVICE_NOTIFICATION_ID, builder.build())
    }

    private fun insertScreenOnTime() {
        batteryMonitoringRepoInterface.insertScreenOnTime(
            screenOnBuffer + ServiceUtil.calculateTimeInterval(
                Date(), batteryMonitoringRepoInterface.getStartTime()
            )
        )
    }

    private fun insertScreenOffTime() {
        batteryMonitoringRepoInterface.insertScreenOffTime(
            screenOffBuffer + ServiceUtil.calculateTimeInterval(
                Date(), batteryMonitoringRepoInterface.getStartTime()
            )
        )
    }

    private fun insertDeepSleepAndAwake() {
        deepSleep =
            deepSleepBuffer + BatteryUtils.calculateDeepSleep(deepSleepInitialValue)
        batteryMonitoringRepoInterface.insertDeepSleepInitialValue(deepSleep)
        cpuAwake =
            batteryMonitoringRepoInterface.getScreenOffTime() - deepSleep
        batteryMonitoringRepoInterface.insertCpuAwake(cpuAwake)
    }

    private fun registerReceiver() {
        val screenStateIntentFilter = IntentFilter()
        screenStateIntentFilter.addAction(Intent.ACTION_SCREEN_ON)
        screenStateIntentFilter.addAction(Intent.ACTION_SCREEN_OFF)
        registerReceiver(screenStateReceiver, screenStateIntentFilter)

        val powerConnectedIntentFilter = IntentFilter()
        powerConnectedIntentFilter.addAction(Intent.ACTION_POWER_DISCONNECTED)
        registerReceiver(powerStateReceiver, powerConnectedIntentFilter)

        val batteryStatIntentFilter = IntentFilter()
        batteryStatIntentFilter.addAction(Intent.ACTION_BATTERY_CHANGED)
        registerReceiver(batteryStateReceiver, batteryStatIntentFilter)
    }

    private fun unregisterReceiver() {
        unregisterReceiver(screenStateReceiver)
        unregisterReceiver(powerStateReceiver)
        unregisterReceiver(batteryStateReceiver)
    }

    inner class ScreenStateReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent?) {
            when (intent?.action) {
                Intent.ACTION_SCREEN_ON -> {
                    screenOn = true
                    insertScreenOffTime()
                    insertDeepSleepAndAwake()
                    batteryMonitoringRepoInterface.insertStartTime(Date())
                    screenOnBuffer = batteryMonitoringRepoInterface.getScreenOnTime()
                }
                Intent.ACTION_SCREEN_OFF -> {
                    screenOn = false
                    insertScreenOnTime()
                    batteryMonitoringRepoInterface.insertStartTime(Date())
                    screenOffBuffer = batteryMonitoringRepoInterface.getScreenOffTime()
                }
            }
        }
    }
}