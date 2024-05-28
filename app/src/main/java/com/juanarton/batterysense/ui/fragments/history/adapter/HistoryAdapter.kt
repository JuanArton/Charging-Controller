package com.juanarton.batterysense.ui.fragments.history.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.juanarton.batterysense.ui.fragments.history.CurrentHistoryFragment
import com.juanarton.batterysense.ui.fragments.history.PowerHistoryFragment
import com.juanarton.batterysense.ui.fragments.history.TempHistoryFragment

class HistoryAdapter (fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {
    override fun getItemCount(): Int = 3
    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> CurrentHistoryFragment()
            1 -> TempHistoryFragment()
            else -> {
                PowerHistoryFragment()
            }
        }
    }
}