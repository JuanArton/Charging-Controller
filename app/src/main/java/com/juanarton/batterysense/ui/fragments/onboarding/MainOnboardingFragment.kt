package com.juanarton.batterysense.ui.fragments.onboarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.juanarton.batterysense.databinding.FragmentMainOnboardingBinding
import com.juanarton.batterysense.ui.fragments.onboarding.adapter.OnboardingAdapter


class MainOnboardingFragment : Fragment() {

    private var _binding: FragmentMainOnboardingBinding? = null
    private val binding get() = _binding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMainOnboardingBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.apply {
            val pagerAdapter = OnboardingAdapter(requireActivity())
            vpOnboarding.adapter = pagerAdapter

            dotsIndicator.attachTo(vpOnboarding)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}