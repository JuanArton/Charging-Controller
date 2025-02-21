package com.juanarton.batterysense.ui.fragments.dashboard.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.juanarton.batterysense.ui.fragments.dashboard.liveupdate.CurrentHistoryFragment
import com.juanarton.batterysense.ui.fragments.dashboard.liveupdate.PowerHistoryFragment
import com.juanarton.batterysense.ui.fragments.dashboard.liveupdate.TempHistoryFragment

class HistoryAdapter (fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {
    override fun getItemCount(): Int = 3
    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> {
                CurrentHistoryFragment()
            }
            1 -> {
                TempHistoryFragment()
            }
            else -> {
                PowerHistoryFragment()
            }
        }
    }
}