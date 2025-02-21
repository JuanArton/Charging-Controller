package com.juanarton.batterysense.ui.fragments.history.util

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.google.android.material.behavior.HideBottomViewOnScrollBehavior

class ScrollAwareBehavior<V : View>(context: Context, attrs: AttributeSet) :
    HideBottomViewOnScrollBehavior<V>(context, attrs) {

    private var listener: OnScrollStateChangedListener? = null

    fun setOnScrollStateChangedListener(listener: OnScrollStateChangedListener) {
        this.listener = listener
    }

    override fun onNestedScroll(
        coordinatorLayout: CoordinatorLayout,
        child: V,
        target: View,
        dxConsumed: Int,
        dyConsumed: Int,
        dxUnconsumed: Int,
        dyUnconsumed: Int,
        type: Int,
        consumed: IntArray
    ) {
        super.onNestedScroll(
            coordinatorLayout,
            child,
            target,
            dxConsumed,
            dyConsumed,
            dxUnconsumed,
            dyUnconsumed,
            type,
            consumed
        )

        if (dyConsumed > 0) {
            listener?.onHidden()
        } else if (dyConsumed < 0) {
            listener?.onShown()
        }
    }

    interface OnScrollStateChangedListener {
        fun onHidden()
        fun onShown()
    }
}
