package com.juanarton.chargingcurrentcontroller.ui.activity.batteryhistory

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.juanarton.chargingcurrentcontroller.R
import com.juanarton.chargingcurrentcontroller.databinding.ActivityBatteryHistoryBinding
import com.juanarton.core.adapter.BatteryHistoryAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BatteryHistoryActivity : AppCompatActivity() {

    private var _binding: ActivityBatteryHistoryBinding? = null
    private val binding get() = _binding

    private val batteryHistoryViewModel: BatteryHistoryViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        _binding = ActivityBatteryHistoryBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding?.rvHistory?.layoutManager = LinearLayoutManager(this)
        val rvAdapter = BatteryHistoryAdapter(this)
        binding?.rvHistory?.adapter = rvAdapter

        batteryHistoryViewModel.getBatteryHistory()

        batteryHistoryViewModel.batteryHistory.observe(this) {
            rvAdapter.setData(it)
            rvAdapter.notifyDataSetChanged()
        }
    }
}