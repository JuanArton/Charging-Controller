package com.juanarton.batterysense.ui.fragments.onboarding

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.DefaultValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import com.juanarton.batterysense.R
import com.juanarton.batterysense.databinding.FragmentIntroductionPageBinding
import com.juanarton.batterysense.utils.BatteryHistoryHolder
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.abs

class IntroductionPageFragment : Fragment() {

    private var _binding: FragmentIntroductionPageBinding? = null
    private val binding get() = _binding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentIntroductionPageBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.apply {
            showChart(
                BatteryHistoryHolder.currentLineDataSet,
                BatteryHistoryHolder.currentData,
                R.drawable.chart_gradient
            )

            viewLifecycleOwner.lifecycleScope.launch {
                while (isActive) {
                    if (BatteryHistoryHolder.batteryCurrent.isNotEmpty()){
                        val currentValue = buildString {
                            append(
                                abs(
                                    BatteryHistoryHolder
                                        .batteryCurrent[BatteryHistoryHolder.batteryCurrent.lastIndex].y.toInt()
                                )
                            )
                            append(getString(R.string.ma))
                        }

                        binding?.tvChartValue?.text = currentValue

                        BatteryHistoryHolder.currentData.notifyDataChanged()
                        binding?.chargingCurrentChart?.notifyDataSetChanged()
                        binding?.chargingCurrentChart?.invalidate()
                    }
                    delay(1 * 1000)
                }
            }
        }
    }

    private fun setUpLineChart(lineDataSet: LineDataSet, lineData: LineData, fillGradient: Drawable?) {
        val typedValue = TypedValue()
        requireContext().theme.resolveAttribute(android.R.attr.colorPrimary, typedValue, true)

        lineDataSet.apply {
            setDrawValues(false)
            setDrawCircles(false)
            axisDependency = YAxis.AxisDependency.RIGHT
            valueFormatter = DefaultValueFormatter(0)
            setDrawFilled(true)
            isHighlightEnabled = false
            fillDrawable = fillGradient
            lineWidth = 2.0F
            mode = LineDataSet.Mode.CUBIC_BEZIER
            color = typedValue.data
        }

        binding?.chargingCurrentChart?.apply {
            xAxis.apply {
                position = XAxis.XAxisPosition.BOTTOM
                setLabelCount(13, true)
                axisMinimum = 0F
                axisMaximum = 60F
                valueFormatter = object : ValueFormatter() {
                    override fun getFormattedValue(value: Float): String = "${60 - value.toInt()}s"
                }
                labelRotationAngle = -45f
                textColor = typedValue.data
            }

            axisRight.apply {
                isEnabled = true
                setLabelCount(8, true)
                granularity = 100F
                valueFormatter = object : ValueFormatter() {
                    override fun getFormattedValue(value: Float): String = value.toInt().toString()
                }
                textColor = typedValue.data
            }

            axisLeft.isEnabled = false
            isAutoScaleMinMaxEnabled = true
            axisRight.enableGridDashedLine(10f, 10f, 0f)
            xAxis.enableGridDashedLine(10f, 10f, 0f)
            description.isEnabled = false
            legend.isEnabled = false
            data = lineData
            animateXY(1000, 1000)
        }
    }

    private fun showChart(lineDataSet: LineDataSet, lineData: LineData, gradientDrawable: Int) {
        val typedValue = TypedValue()
        requireContext().theme.resolveAttribute(android.R.attr.colorPrimary, typedValue, true)

        setUpLineChart(lineDataSet, lineData, ContextCompat.getDrawable(requireContext(), gradientDrawable))
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
