package com.juanarton.batterysense.ui.fragments.history.charging

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.juanarton.batterysense.databinding.FragmentChargingHistoryBinding
import com.juanarton.core.adapter.ChargingHistoryAdapter
import com.juanarton.core.data.domain.batteryMonitoring.domain.ChargingHistory
import dagger.hilt.android.AndroidEntryPoint
import java.util.Calendar
import java.util.TimeZone

@AndroidEntryPoint
class ChargingHistoryFragment(private val listener: (ChargingHistory) -> Unit) : Fragment() {

    private var _binding: FragmentChargingHistoryBinding? = null
    private val binding get() = _binding
    private var day = ""
    private var listChargingHistory: ArrayList<ChargingHistory> = arrayListOf()
    private lateinit var rvAdapter: ChargingHistoryAdapter

    private val chargingHistoryViewModel: ChargingHistoryViewModel by viewModels()

    fun setDay(day: String) {
        try {
            this.day = day
            chargingHistoryViewModel.getChargingHistoryByDay(day)
        } catch (e: Exception) {}
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentChargingHistoryBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.apply {

            binding?.rvHistory?.layoutManager = LinearLayoutManager(requireContext())
            rvAdapter = ChargingHistoryAdapter(listener, requireContext())
            binding?.rvHistory?.adapter = rvAdapter

            chargingHistoryViewModel.chargingHistoryByDay.observe(viewLifecycleOwner) {
                rvHistory.visibility = View.VISIBLE
                listChargingHistory.addAll(it)
                if (it.isEmpty()) {
                    rvHistory.visibility = View.GONE
                }
                rvAdapter.setData(it)
            }
        }
    }

    fun updateData(minHour: Int, maxHour: Int) {
        if (::rvAdapter.isInitialized) {
            val filtered = filterHistory(listChargingHistory, minHour, maxHour)
            rvAdapter.setData(filtered)

            binding?.apply {
                rvHistory.visibility = View.VISIBLE
                if (filtered.isEmpty()) {
                    rvHistory.visibility = View.GONE
                }
            }
        }
    }

    private fun filterHistory(
        historyList: List<ChargingHistory>, minHour: Int, maxHour: Int
    ): List<ChargingHistory> {
        return historyList.filter { history ->
            val calendar = Calendar.getInstance(TimeZone.getDefault()).apply {
                timeInMillis = history.endTime
            }
            val endHour = calendar.get(Calendar.HOUR_OF_DAY)
            endHour in minHour..maxHour
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        chargingHistoryViewModel.getChargingHistoryByDay(day)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}