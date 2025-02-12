package com.juanarton.batterysense.ui.fragments.history

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.slider.RangeSlider
import com.google.android.material.tabs.TabLayoutMediator
import com.juanarton.batterysense.R
import com.juanarton.batterysense.databinding.FragmentHistoryBinding
import com.juanarton.batterysense.ui.activity.batteryhistory.adapter.DetailedHistoryAdapter
import com.juanarton.batterysense.ui.activity.batteryhistory.adapter.FullHistoryAdapter
import com.juanarton.batterysense.ui.activity.detailHistory.DetailHistoryActivity
import com.juanarton.batterysense.ui.fragments.fullhistory.FullHistoryFragment
import com.juanarton.batterysense.ui.fragments.fullhistory.chart.ChartFullHistoryFragment
import com.juanarton.batterysense.ui.fragments.history.charging.ChargingHistoryFragment
import com.juanarton.batterysense.ui.fragments.history.util.ScrollAwareBehavior
import com.juanarton.batterysense.utils.FragmentUtil.dpToPx
import com.juanarton.batterysense.utils.Utils.animateHeight
import com.juanarton.core.data.domain.batteryMonitoring.domain.ChargingHistory
import com.topjohnwu.superuser.internal.UiThreadHandler.handler
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class BatteryHistoryFragment : Fragment() {

    private var _binding: FragmentHistoryBinding? = null
    private val binding get() =  _binding

    private val batteryHistoryViewModel: BatteryHistoryViewModel by viewModels()
    private var isHide = false
    private var currentFragment: ChartFullHistoryFragment? = null
    private lateinit var pagerAdapter: FullHistoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHistoryBinding.inflate(inflater, container, false)
        return binding?.root
    }

    @SuppressLint("DefaultLocale")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val listener: (ChargingHistory) -> Unit = { chargingHistory ->
            currentFragment?.let {
                val day = it.day

                val intent = Intent(requireContext(), DetailHistoryActivity::class.java)
                intent.putExtra("DAY", day)
                intent.putExtra("HISTORY", chargingHistory)
                startActivity(intent)
            }
        }
        val detailedHistoryAdapter = DetailedHistoryAdapter(requireActivity(), listener)

        binding?.apply {
            batteryHistoryViewModel.getAvailableDay()
            batteryHistoryViewModel.availableDay.observe(viewLifecycleOwner) {
                pagerAdapter = FullHistoryAdapter(requireActivity(), it)
                vpFullHistoryChart.adapter = pagerAdapter
            }

            val runnable = Runnable {
                if (vpFullHistoryChart.adapter != null) {
                    fullHistoryDotsIndicator.attachTo(vpFullHistoryChart)
                }
            }
            val delayMillis: Long = 1000
            handler.postDelayed(runnable, delayMillis)

            vpDetailedHistory.adapter = detailedHistoryAdapter
            TabLayoutMediator(tlDetailedHistory, vpDetailedHistory) { tab, position ->
                tab.text = when (position) {
                    0 -> getString(R.string.charging_history)
                    1 -> getString(R.string.full_history)
                    else -> "Tab ${position + 1}"
                }
            }.attach()

            vpFullHistoryChart.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)

                    if (::pagerAdapter.isInitialized) {
                        currentFragment = pagerAdapter.getFragment(position)
                        currentFragment?.let {
                            it.day.let { day ->
                                (detailedHistoryAdapter.fragments[0] as ChargingHistoryFragment).setDay(day)

                                lifecycleScope.launch {
                                    while (true) {
                                        val historyList = it.getHistoryList()
                                        if (historyList.isNotEmpty()) {
                                            (detailedHistoryAdapter.fragments[1] as FullHistoryFragment).setData(historyList)
                                            break
                                        }
                                        delay(500)
                                    }
                                }
                            }
                        }
                    }
                }
            })

            val bottomNav = requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigationBar)
            val layoutParams = bottomNav.layoutParams as CoordinatorLayout.LayoutParams
            val behavior = layoutParams.behavior as? ScrollAwareBehavior

            behavior?.setOnScrollStateChangedListener(object : ScrollAwareBehavior.OnScrollStateChangedListener {
                override fun onHidden() {
                    if (!isHide) {
                        isHide = true
                        animateHeight(llHistoryChart, llHistoryChart.height, 0)
                    }
                }

                override fun onShown() {
                    if (isHide) {
                        isHide = false
                        animateHeight(llHistoryChart, 0, dpToPx(275, requireContext()))
                    }
                }
            })

            rsTimeHistory.values = listOf(0f, 24f)
            rsTimeHistory.setMinSeparationValue(3F)
            tvRangeMax.text = buildString {
                append(String.format("%02d", 24))
                append(":00")
            }
            tvRangeMin.text = buildString {
                append(String.format("%02d", 0))
                append(":00")
            }

            rsTimeHistory.addOnSliderTouchListener(object : RangeSlider.OnSliderTouchListener{
                override fun onStartTrackingTouch(slider: RangeSlider) {}

                @SuppressLint("DefaultLocale")
                override fun onStopTrackingTouch(slider: RangeSlider) {
                    val min = slider.values[0].toInt()
                    val max = slider.values[1].toInt()

                    tvRangeMax.text = buildString {
                        append(String.format("%02d", max))
                        append(":00")
                    }
                    tvRangeMin.text = buildString {
                        append(String.format("%02d", min))
                        append(":00")
                    }

                    currentFragment?.updateData(min, max)
                    (detailedHistoryAdapter.fragments[0] as ChargingHistoryFragment).updateData(min, max)
                    currentFragment?.let {
                        (detailedHistoryAdapter.fragments[1] as FullHistoryFragment).updateData(
                            min, max
                        )
                    }
                }
            })

            fabShowChart.setOnClickListener {
                isHide = false
                animateHeight(llHistoryChart, 0, dpToPx(275, requireContext()))
            }
        }
    }
}