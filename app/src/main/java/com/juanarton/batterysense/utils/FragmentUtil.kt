package com.juanarton.batterysense.utils

import android.view.View
import android.view.ViewGroup

object FragmentUtil {
    var isEmitting: Boolean = false

    fun minChecker(oldValue: Int, newValue: Int): Int {
        return if (newValue < oldValue ) newValue else oldValue
    }

    fun maxChecker(oldValue: Int, newValue: Int): Int {
        return if (newValue > oldValue ) newValue else oldValue
    }

    fun rescaleNumber(input: Int): Int {
        return (input.toDouble() / 100.0 * 246.0).toInt()
    }

    fun changeViewHeight(view: View, heightInDp: Int) {
        val density = view.resources.displayMetrics.density
        val heightInPixels = (heightInDp * density).toInt()

        val layoutParams: ViewGroup.LayoutParams = view.layoutParams
        layoutParams.height = heightInPixels
        view.layoutParams = layoutParams
    }
}