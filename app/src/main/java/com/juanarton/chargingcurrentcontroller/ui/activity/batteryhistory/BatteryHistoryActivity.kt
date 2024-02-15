package com.juanarton.chargingcurrentcontroller.ui.activity.batteryhistory

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.juanarton.chargingcurrentcontroller.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BatteryHistoryActivity : AppCompatActivity() {

    private val batteryHistoryViewModel: BatteryHistoryViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_battery_history)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        batteryHistoryViewModel.getBatteryHistory().observe(this) {
            Log.d("test", it.toString())
        }
    }
}