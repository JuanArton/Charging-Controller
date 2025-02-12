package com.juanarton.batterysense.ui.activity.batteryhistory.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.juanarton.batterysense.ui.fragments.fullhistory.FullHistoryFragment
import com.juanarton.batterysense.ui.fragments.history.charging.ChargingHistoryFragment
import com.juanarton.core.data.domain.batteryMonitoring.domain.ChargingHistory

class DetailedHistoryAdapter (fragmentActivity: FragmentActivity, listener: (ChargingHistory) -> Unit) :
    FragmentStateAdapter(fragmentActivity) {

    val fragments = listOf(ChargingHistoryFragment(listener), FullHistoryFragment())

    override fun getItemCount(): Int = fragments.size

    override fun createFragment(position: Int): Fragment = fragments[position]
}