package com.juanarton.batterysense.ui.activity.main

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
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
import com.google.android.material.behavior.HideBottomViewOnScrollBehavior
import com.juanarton.batterysense.BuildConfig
import com.juanarton.batterysense.R
import com.juanarton.batterysense.batterymonitorservice.Action
import com.juanarton.batterysense.batterymonitorservice.BatteryMonitorService
import com.juanarton.batterysense.batterymonitorservice.BatteryMonitorService.Companion.isRegistered
import com.juanarton.batterysense.batterymonitorservice.ServiceState
import com.juanarton.batterysense.batterymonitorservice.getServiceState
import com.juanarton.batterysense.databinding.ActivityMainBinding
import com.juanarton.batterysense.ui.activity.about.AboutActivity
import com.juanarton.batterysense.ui.activity.setting.SettingsActivity
import com.juanarton.batterysense.ui.fragments.alarm.AlarmFragment
import com.juanarton.batterysense.ui.fragments.charging.ChargingFragment
import com.juanarton.batterysense.ui.fragments.discharging.DischargingFragment
import com.juanarton.batterysense.ui.fragments.history.HistoryFragment
import com.juanarton.batterysense.ui.fragments.onboarding.MainOnboardingFragment
import com.juanarton.batterysense.ui.fragments.quicksetting.QuickSettingFragment
import com.juanarton.batterysense.utils.FragmentUtil.isEmitting
import com.juanarton.core.utils.BatteryUtils.getChargingStatus
import com.juanarton.core.utils.BatteryUtils.registerStickyReceiver
import com.topjohnwu.superuser.Shell
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val mainPowerStateReceiver = MainPowerStateReceiver()
    private lateinit var currentFragment: String
    private var isForeground = true
    private lateinit var powerStateIntent: Intent
    private var isPowerStateRegistered = false
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
            fragmentBuilder(MainOnboardingFragment(), R.id.root, "Onboarding", "")
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

            registerStickyReceiver(this)

            val isCharging = getChargingStatus()
            if (isCharging != 1 && isCharging != 3) {
                val title = getString(com.juanarton.core.R.string.charging)
                fragmentBuilder(ChargingFragment(), R.id.fragmentHolder, "Charging", title)
                binding?.bottomNavigationBar?.selectedItemId = R.id.charging
            }
            else {
                val title = getString(com.juanarton.core.R.string.discharging)
                fragmentBuilder(DischargingFragment(), R.id.fragmentHolder, "Discharging", title)
                binding?.bottomNavigationBar?.selectedItemId = R.id.discharging
            }

            registerReceiver()

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
                        R.id.charging -> {
                            val title = getString(com.juanarton.core.R.string.charging)
                            fragmentBuilder(ChargingFragment(), holder, "Charging", title)
                            true
                        }
                        R.id.discharging -> {
                            val title = getString(com.juanarton.core.R.string.discharging)
                            fragmentBuilder(DischargingFragment(), holder, "Discharging", title)
                            true
                        }
                        R.id.quickSetting -> {
                            val title = getString(R.string.quick_setting)
                            fragmentBuilder(QuickSettingFragment(), holder, "QuickSetting", title)
                            true
                        }
                        R.id.alarm -> {
                            val title = getString(R.string.alarm)
                            fragmentBuilder(AlarmFragment(), holder, "Alarm", title)
                            true
                        }
                        R.id.history -> {
                            val title = getString(R.string.history)
                            fragmentBuilder(HistoryFragment(), holder, "History", title)
                            true
                        }
                        else -> false
                    }
                }
            }
        }
    }

    private fun fragmentBuilder(fragment: Fragment, holder: Int, tag: String, title: String){
        currentFragment = tag
        supportFragmentManager
            .beginTransaction()
            .replace(holder, fragment, tag)
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
            .commit()


        binding?.tvTitle?.text = title
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

    private fun registerReceiver() {
        val powerConnectedIntentFilter = IntentFilter()
        powerConnectedIntentFilter.addAction(Intent.ACTION_POWER_DISCONNECTED)
        powerConnectedIntentFilter.addAction(Intent.ACTION_POWER_CONNECTED)
        registerReceiver(mainPowerStateReceiver, powerConnectedIntentFilter)
        isPowerStateRegistered = true
    }

    private fun checkPowerStateIntent() {
        if (::powerStateIntent.isInitialized) {
            when(powerStateIntent.action) {
                Intent.ACTION_POWER_DISCONNECTED -> {
                    if (isForeground) {
                        val title = getString(com.juanarton.core.R.string.discharging)
                        fragmentBuilder(DischargingFragment(), R.id.fragmentHolder, "Discharging", title)
                        binding?.bottomNavigationBar?.selectedItemId = R.id.discharging
                        isEmitting = false
                        unhiddenBottomNavigation()
                    }
                }
                Intent.ACTION_POWER_CONNECTED -> {
                    if (isForeground) {
                        val title = getString(com.juanarton.core.R.string.charging)
                        fragmentBuilder(ChargingFragment(), R.id.fragmentHolder, "Charging", title)
                        binding?.bottomNavigationBar?.selectedItemId = R.id.charging
                        isEmitting = true
                        unhiddenBottomNavigation()
                    }
                }
            }
        }
    }

    private fun unhiddenBottomNavigation() {
        val layoutParams = binding?.bottomNavigationBar?.layoutParams as CoordinatorLayout.LayoutParams
        val bottomViewNavigationBehavior = layoutParams.behavior as HideBottomViewOnScrollBehavior
        bottomViewNavigationBehavior.slideUp(binding!!.bottomNavigationBar)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.settings_page -> {
                startActivity(Intent(this, SettingsActivity::class.java))
                true
            }
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
        if (isPowerStateRegistered) {
            unregisterReceiver(mainPowerStateReceiver)
        }
    }

    override fun onResume() {
        super.onResume()
        isForeground = true
        checkPowerStateIntent()
    }

    override fun onPause() {
        super.onPause()
        isForeground = false
    }

    inner class MainPowerStateReceiver: BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            powerStateIntent = intent
            checkPowerStateIntent()
        }
    }
}