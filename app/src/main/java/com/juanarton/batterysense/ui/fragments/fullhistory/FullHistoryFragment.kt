package com.juanarton.batterysense.ui.fragments.fullhistory

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.juanarton.batterysense.databinding.FragmentFullHistoryBinding
import com.juanarton.batterysense.ui.fragments.fullhistory.adapter.FullHistoryAdapter
import com.juanarton.core.data.domain.batteryMonitoring.domain.BatteryHistory
import java.util.Calendar
import java.util.TimeZone

class FullHistoryFragment : Fragment() {

    private var _binding: FragmentFullHistoryBinding? = null
    private val binding get() = _binding

    private lateinit var rvAdapter: FullHistoryAdapter
    private var listBatteryHistory: ArrayList<BatteryHistory> = arrayListOf()
    private var filteredHistoryList: ArrayList<BatteryHistory> = arrayListOf()

    fun setData(batteryHistory: List<BatteryHistory>) {
        listBatteryHistory.clear()
        listBatteryHistory.addAll(batteryHistory.asReversed())

        if (::rvAdapter.isInitialized) {
            rvAdapter.setData(listBatteryHistory)
        }
    }

    fun updateData(minHour: Int, maxHour: Int) {
        filteredHistoryList.clear()
        filteredHistoryList.addAll(filterHistory(listBatteryHistory, minHour, maxHour))

        binding?.apply {
            rvAdapter.setData(filteredHistoryList)
        }
    }

    private fun filterHistory(
        historyList: List<BatteryHistory>, minHour: Int, maxHour: Int
    ): List<BatteryHistory> {
        return historyList.filter { history ->
            val calendar = Calendar.getInstance(TimeZone.getDefault()).apply {
                timeInMillis = history.timestamp
            }
            val timestamp = calendar.get(Calendar.HOUR_OF_DAY)
            timestamp in minHour..maxHour
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFullHistoryBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.apply {
            rvFullHistory.layoutManager = LinearLayoutManager(requireContext())
            rvAdapter = FullHistoryAdapter(requireContext())
            rvFullHistory.adapter = rvAdapter

            rvAdapter.setData(listBatteryHistory)
        }
    }
}