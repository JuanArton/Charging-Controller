package com.juanarton.batterysense.ui.activity.batteryhistory.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.juanarton.batterysense.ui.fragments.fullhistory.chart.ChartFullHistoryFragment

class FullHistoryAdapter(
    fragmentActivity: FragmentActivity,
    private val listDay: List<String>
) : FragmentStateAdapter(fragmentActivity) {

    private val fragmentMap = mutableMapOf<Int, ChartFullHistoryFragment>()

    override fun getItemCount(): Int = listDay.size

    override fun createFragment(position: Int): Fragment {
        return ChartFullHistoryFragment.newInstance(listDay[position]).also {
            fragmentMap[position] = it
        }
    }

    fun getFragment(position: Int): ChartFullHistoryFragment? {
        return fragmentMap[position]
    }
}