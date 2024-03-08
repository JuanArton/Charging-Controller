package com.juanarton.batterysense.ui.fragments.onboarding.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.juanarton.batterysense.ui.fragments.onboarding.CalibrationPageFragment
import com.juanarton.batterysense.ui.fragments.onboarding.IntroductionPageFragment
import com.juanarton.batterysense.ui.fragments.onboarding.WelcomePageFragment

class OnboardingAdapter (fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {
    override fun getItemCount(): Int = 3
    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> WelcomePageFragment()
            1 -> IntroductionPageFragment()
            else -> {
                CalibrationPageFragment()
            }
        }
    }
}