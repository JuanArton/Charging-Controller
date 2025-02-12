package com.juanarton.batterysense.ui.fragments.history.util

import android.content.Context
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.text.SpannableString
import android.text.Spanned
import android.text.TextUtils
import android.text.style.AbsoluteSizeSpan
import android.util.TypedValue
import androidx.core.graphics.ColorUtils
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.DefaultValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter


object HistoryUtil {
    fun showHistoryChart(
        lineDataSet: LineDataSet, lineData: LineData, context: Context, historyChart: LineChart?
    ) {
        val typedValue = TypedValue()
        context.theme.resolveAttribute(android.R.attr.colorPrimary, typedValue, true)

        val drawable = createGradient(typedValue.data)

        setUpLineChart(
            lineDataSet, lineData, drawable,
            typedValue, historyChart
        )
    }

    private fun setUpLineChart(
        lineDataSet: LineDataSet, lineData: LineData, fillGradient: Drawable?,
        typedValue: TypedValue, historyChart: LineChart?
    ) {
        lineDataSet.apply {
            setDrawValues(false)
            setDrawCircles(false)
            axisDependency = YAxis.AxisDependency.RIGHT
            valueFormatter = DefaultValueFormatter(0)
            setDrawFilled(true)
            isHighlightEnabled = false
            fillDrawable = fillGradient
            lineWidth = 2.0F
            mode = LineDataSet.Mode.LINEAR
            color = typedValue.data
        }

        historyChart?.apply {
            xAxis.apply {
                isEnabled = true
                setDrawGridLines(false)
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

            axisRight.isEnabled = false
            axisLeft.isEnabled = false
            isAutoScaleMinMaxEnabled = true
            description.isEnabled = false
            legend.isEnabled = false
            data = lineData
            animateXY(1000, 1000)
        }
    }

    fun changeChartColor(
        historyChart: LineChart?, lineDataSet: LineDataSet, newColor: Int
    ) {
        val fillGradient = createGradient(newColor)
        lineDataSet.apply {
            fillDrawable = fillGradient
            color = newColor
        }

        historyChart?.apply {
            xAxis.apply {
                textColor = newColor
            }
        }
    }

    fun createGradient(color: Int): Drawable {
        return GradientDrawable().apply {
            colors = intArrayOf(
                ColorUtils.setAlphaComponent(color, 50),
                ColorUtils.setAlphaComponent(color, 10)
            )
            orientation = GradientDrawable.Orientation.TOP_BOTTOM
            gradientType = GradientDrawable.LINEAR_GRADIENT
            shape = GradientDrawable.RECTANGLE
        }
    }

    fun createStringValue(value: String, unit: String, context: Context): CharSequence {
        val currentValue = SpannableString(value)
        currentValue.setSpan(
            AbsoluteSizeSpan(convertToSp(context, 30)),
            0,
            currentValue.length,
            Spanned.SPAN_INCLUSIVE_INCLUSIVE
        )

        val valueUnit = SpannableString(unit)
        valueUnit.setSpan(
            AbsoluteSizeSpan(convertToSp(context, 14)),
            0,
            valueUnit.length,
            Spanned.SPAN_INCLUSIVE_INCLUSIVE
        )

        return TextUtils.concat(currentValue, " ", valueUnit)
    }

    private fun convertToSp(context: Context, value: Int): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_SP,
            value.toFloat(),
            context.resources.displayMetrics
        ).toInt()
    }
}