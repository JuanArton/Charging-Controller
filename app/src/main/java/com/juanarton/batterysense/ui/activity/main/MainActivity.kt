package com.juanarton.batterysense.ui.activity.main

import android.Manifest
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.FrameLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
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
import com.juanarton.batterysense.batterymonitorservice.Action
import com.juanarton.batterysense.batterymonitorservice.BatteryMonitorService
import com.juanarton.batterysense.batterymonitorservice.BatteryMonitorService.Companion.isRegistered
import com.juanarton.batterysense.batterymonitorservice.ServiceState
import com.juanarton.batterysense.batterymonitorservice.getServiceState
import com.juanarton.batterysense.databinding.ActivityMainBinding
import com.juanarton.batterysense.ui.activity.about.AboutActivity
import com.juanarton.batterysense.ui.fragments.alarm.AlarmFragment
import com.juanarton.batterysense.ui.fragments.dashboard.DashboardFragment
import com.juanarton.batterysense.ui.fragments.history.HistoryFragment
import com.juanarton.batterysense.ui.fragments.onboarding.MainOnboardingFragment
import com.juanarton.batterysense.ui.fragments.quicksetting.QuickSettingFragment
import com.topjohnwu.superuser.Shell
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

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

        setSupportActionBar(binding?.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        setFullScreen()

        val settings = getSharedPreferences(PREFS_NAME, 0)

        actionOnService(Action.START)
        splashScreen.setKeepOnScreenCondition { false }

        if (settings.getBoolean("first_launch", true)) {
            binding?.bottomNavigationBar?.visibility = View.GONE
            fragmentBuilder(MainOnboardingFragment(), R.id.root, "Onboarding")
        } else {
            CoroutineScope(Dispatchers.IO).launch {
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

            fragmentBuilder(DashboardFragment(), R.id.fragmentHolder, "Dashboard")

            if(!isRegistered) {
                val intentFilter = IntentFilter()
                intentFilter.addAction(Intent.ACTION_SCREEN_ON)
                intentFilter.addAction(Intent.ACTION_SCREEN_OFF)

                isRegistered = true
            }

            binding?.apply {
                val typedValue = TypedValue()
                this@MainActivity.theme.resolveAttribute(android.R.attr.textColor, typedValue, true)
                toolbar.overflowIcon?.setTint(typedValue.data)

                val holder = R.id.fragmentHolder

                bottomNavigationBar.setOnItemSelectedListener { menuItem ->
                    when (menuItem.itemId) {
                        R.id.dashboard -> {
                            fragmentBuilder(DashboardFragment(), holder, "Dashboard")
                            true
                        }
                        R.id.quickSetting -> {
                            fragmentBuilder(QuickSettingFragment(), holder, "QuickSetting")
                            true
                        }
                        R.id.alarm -> {
                            fragmentBuilder(AlarmFragment(), holder, "Alarm")
                            true
                        }
                        R.id.history -> {
                            fragmentBuilder(HistoryFragment(), holder, "History")
                            true
                        }
                        else -> false
                    }
                }
            }
        }
    }

    private fun fragmentBuilder(fragment: Fragment, holder: Int, tag: String){
        supportFragmentManager
            .beginTransaction()
            .replace(holder, fragment, tag)
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
        val root = findViewById<CoordinatorLayout>(R.id.root)

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

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.about_page -> {
                startActivity(Intent(this, AboutActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}