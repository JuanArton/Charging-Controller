package com.juanarton.batterysense.ui.fragments.history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.juanarton.batterysense.databinding.FragmentHistoryBinding
import com.juanarton.core.adapter.ChargingHistoryAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HistoryFragment : Fragment() {

    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding

    private val historyViewModel: HistoryViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHistoryBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        historyViewModel.getChargingHistory()

        binding?.rvHistory?.layoutManager = LinearLayoutManager(requireContext())
        val rvAdapter = ChargingHistoryAdapter(requireContext())
        binding?.rvHistory?.adapter = rvAdapter

        historyViewModel.chargingHistory.observe(viewLifecycleOwner) {
            rvAdapter.setData(it)
            rvAdapter.notifyDataSetChanged()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}