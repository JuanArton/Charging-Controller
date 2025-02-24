package com.juanarton.batterysense.ui.activity.main

import android.Manifest
import android.content.ComponentName
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
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
import com.juanarton.batterysense.batterymonitorservice.ServiceState
import com.juanarton.batterysense.batterymonitorservice.getServiceState
import com.juanarton.batterysense.databinding.ActivityMainBinding
import com.juanarton.batterysense.ui.activity.about.AboutActivity
import com.juanarton.batterysense.ui.activity.setting.SettingsActivity
import com.juanarton.batterysense.ui.fragments.alarm.AlarmFragment
import com.juanarton.batterysense.ui.fragments.dashboard.DashboardFragment
import com.juanarton.batterysense.ui.fragments.history.BatteryHistoryFragment
import com.juanarton.batterysense.ui.fragments.onboarding.MainOnboardingFragment
import com.juanarton.batterysense.ui.fragments.quicksetting.QuickSettingFragment
import com.juanarton.batterysense.utils.FragmentUtil.dpToPx
import com.juanarton.core.utils.BatteryUtils.registerStickyReceiver
import com.topjohnwu.superuser.Shell
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import xyz.kumaraswamy.autostart.Autostart


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var currentFragment: String
    private var isForeground = true
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
            supportActionBar?.hide()
            fragmentBuilder(MainOnboardingFragment(), R.id.root, "Onboarding", "")
        } else {
            CoroutineScope(Dispatchers.IO).launch {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    val result = permissionsBuilder(Manifest.permission.POST_NOTIFICATIONS)
                        .build()
                        .sendSuspend()
                    if (result.allDenied()) {
                        runOnUiThread {
                            Toast.makeText(
                                this@MainActivity, getString(R.string.notificationPermissionDenied),
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                }
            }

            registerStickyReceiver(this)

            fragmentBuilder(DashboardFragment(), R.id.fragmentHolder, "Charging", getString(R.string.dashboard))

            if (!Autostart.getSafeState(this)) {
                showAutostartDialog()
            }

            binding?.apply {
                val typedValue = TypedValue()
                this@MainActivity.theme.resolveAttribute(android.R.attr.textColor, typedValue, true)
                toolbar.overflowIcon?.setTint(typedValue.data)

                val holder = R.id.fragmentHolder

                bottomNavigationBar.setOnItemSelectedListener { menuItem ->
                    tvTitle.visibility = View.VISIBLE
                    val params = tvTitle.layoutParams as Toolbar.LayoutParams
                    params.topMargin = dpToPx(60, this@MainActivity)

                    when (menuItem.itemId) {
                        R.id.dashboard -> {
                            val title = getString(R.string.dashboard)
                            fragmentBuilder(DashboardFragment(), holder, "Dashboard", title)
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
                            //tvTitle.visibility = View.GONE
                            params.topMargin = dpToPx(0, this@MainActivity)
                            fragmentBuilder(BatteryHistoryFragment(), holder, "History", title)
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

    private fun showAutostartDialog() {
        val dialogView: View = LayoutInflater.from(this).inflate(R.layout.custom_dialog_box, null)

        val dialogMessage = dialogView.findViewById<TextView>(R.id.dialog_message)
        dialogMessage.text = getString(R.string.autostart_dialog_message)

        val okButton = dialogView.findViewById<Button>(R.id.dialog_ok_button)

        val autostartDialogBuilder = AlertDialog.Builder(this)
        autostartDialogBuilder.setView(dialogView)
        autostartDialogBuilder.setCancelable(true)

        val autostartDialog = autostartDialogBuilder.create()

        okButton.setOnClickListener {
            autostartDialog.dismiss()
            startActivity(
                Intent().setComponent(
                ComponentName(
                    "com.miui.securitycenter",
                    "com.miui.permcenter.autostart.AutoStartManagementActivity"
                )
            ))
        }

        autostartDialog.show()
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
    }

    override fun onResume() {
        super.onResume()
        isForeground = true
    }

    override fun onPause() {
        super.onPause()
        isForeground = false
    }
}