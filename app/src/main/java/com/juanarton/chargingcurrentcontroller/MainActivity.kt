package com.juanarton.chargingcurrentcontroller

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.juanarton.chargingcurrentcontroller.databinding.ActivityMainBinding
import com.juanarton.chargingcurrentcontroller.ui.fragments.alarm.AlarmFragment
import com.juanarton.chargingcurrentcontroller.ui.fragments.dashboard.DashboardFragment
import com.juanarton.chargingcurrentcontroller.ui.fragments.quicksetting.QuickSettingFragment
import com.topjohnwu.superuser.Shell
import dagger.hilt.android.AndroidEntryPoint
import nl.joery.animatedbottombar.AnimatedBottomBar

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
    }

    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        splashScreen.setKeepOnScreenCondition { true }

        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)

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

        Shell.getShell { shell ->
            if (shell.status == 1) {
                splashScreen.setKeepOnScreenCondition { false }
            } else {
                startActivity(Intent(this@MainActivity, RootCheckActivity::class.java))
            }
        }

        fragmentBuilder(DashboardFragment())

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
}