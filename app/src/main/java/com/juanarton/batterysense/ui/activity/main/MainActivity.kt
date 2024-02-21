package com.juanarton.batterysense.ui.activity.main

import android.Manifest
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.fondesa.kpermissions.allDenied
import com.fondesa.kpermissions.coroutines.sendSuspend
import com.fondesa.kpermissions.extension.permissionsBuilder
import com.juanarton.batterysense.BuildConfig
import com.juanarton.batterysense.R
import com.juanarton.batterysense.RootCheckActivity
import com.juanarton.batterysense.batterymonitorservice.Action
import com.juanarton.batterysense.batterymonitorservice.BatteryMonitorService
import com.juanarton.batterysense.batterymonitorservice.BatteryMonitorService.Companion.isRegistered
import com.juanarton.batterysense.batterymonitorservice.ServiceState
import com.juanarton.batterysense.batterymonitorservice.getServiceState
import com.juanarton.batterysense.databinding.ActivityMainBinding
import com.juanarton.batterysense.ui.fragments.alarm.AlarmFragment
import com.juanarton.batterysense.ui.fragments.dashboard.DashboardFragment
import com.juanarton.batterysense.ui.fragments.history.HistoryFragment
import com.juanarton.batterysense.ui.fragments.quicksetting.QuickSettingFragment
import com.topjohnwu.superuser.Shell
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import nl.joery.animatedbottombar.AnimatedBottomBar


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val mainActivityViewModel: MainActivityViewModel by viewModels()

    companion object {
        init {
            Shell.enableVerboseLogging = BuildConfig.DEBUG
            Shell.setDefaultBuilder(
                Shell.Builder.create()
                .setFlags(Shell.FLAG_REDIRECT_STDERR)
                .setTimeout(10)
            )
        }

        const val PREFS_NAME = "FirstLaunchState"
    }

    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        splashScreen.setKeepOnScreenCondition { true }

        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        setFullScreen()

        val settings = getSharedPreferences(PREFS_NAME, 0)

        lateinit var job: Job

        if (settings.getBoolean("first_launch", true)) {
            job = CoroutineScope(Dispatchers.IO).launch {
                mainActivityViewModel.insertInitialValue(this@MainActivity)
            }
            settings.edit().putBoolean("first_launch", false).apply()
        }

        CoroutineScope(Dispatchers.IO).launch {
            try {
                while (!job.isCompleted) { }
                actionOnService(Action.START)
            } catch (e: Exception) {
                actionOnService(Action.START)
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                val result = permissionsBuilder(Manifest.permission.POST_NOTIFICATIONS)
                    .build()
                    .sendSuspend()
                if (result.allDenied()) {
                    Toast.makeText(
                        this@MainActivity, getString(R.string.notificationPermissionDenied),
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }


        Shell.getShell { shell ->
            if (shell.status == 1) {
                splashScreen.setKeepOnScreenCondition { false }
            } else {
                startActivity(Intent(this@MainActivity, RootCheckActivity::class.java))
            }
        }

        fragmentBuilder(DashboardFragment())

        if(!isRegistered) {
            val intentFilter = IntentFilter()
            intentFilter.addAction(Intent.ACTION_SCREEN_ON)
            intentFilter.addAction(Intent.ACTION_SCREEN_OFF)

            isRegistered = true
        }

        binding?.apply {
            bottomNavigationBar.setOnTabSelectListener(object: AnimatedBottomBar.OnTabSelectListener{
                override fun onTabSelected(
                    lastIndex: Int,
                    lastTab: AnimatedBottomBar.Tab?,
                    newIndex: Int,
                    newTab: AnimatedBottomBar.Tab
                ) {
                    when(newIndex){
                        0 -> fragmentBuilder(DashboardFragment())
                        1 -> fragmentBuilder(QuickSettingFragment())
                        2 -> fragmentBuilder(AlarmFragment())
                        3 -> fragmentBuilder(HistoryFragment())
                    }
                }

            })
        }
    }

    private fun fragmentBuilder(fragment: Fragment){
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragmentHolder, fragment)
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
            .commit()
    }

    private fun actionOnService(action: Action) {
        if (getServiceState(this) == ServiceState.STOPPED && action == Action.STOP) return
        Intent(this, BatteryMonitorService::class.java).also {
            it.action = action.name
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                Log.d("BatteryMonitorService", "Starting the service in >=26 Mode")
                startForegroundService(it)
                return
            }
            Log.d("BatteryMonitorService", "Starting the service in < 26 Mode")
            startService(it)
        }
    }

    private fun setFullScreen() {
        val root = findViewById<ConstraintLayout>(R.id.root)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.setDecorFitsSystemWindows(false)

            ViewCompat.setOnApplyWindowInsetsListener(root) { view, windowInsets ->

                val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
                view.layoutParams =  (view.layoutParams as FrameLayout.LayoutParams).apply {
                    leftMargin = insets.left
                    bottomMargin = insets.bottom
                    rightMargin = insets.right
                    topMargin = insets.top
                }
                WindowInsetsCompat.CONSUMED
            }
        } else {
            window.decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            window.statusBarColor = android.graphics.Color.TRANSPARENT
            ViewCompat.setOnApplyWindowInsetsListener(root) { view, windowInsets ->

                val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
                view.layoutParams =  (view.layoutParams as FrameLayout.LayoutParams).apply {
                    topMargin = insets.top
                }
                WindowInsetsCompat.CONSUMED
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}