package com.juanarton.core.customview

import android.content.Context
import android.util.AttributeSet
import android.widget.RelativeLayout
import android.widget.TextView
import com.google.android.material.materialswitch.MaterialSwitch
import com.juanarton.core.R

class SwitchWithDescription @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : RelativeLayout(context, attrs, defStyleAttr) {

    private val title: TextView
    private val content: TextView
    private val switch: MaterialSwitch
    private var switchStateChangeListener: OnSwitchStateChangeListener? = null

    init {
        inflate(context, R.layout.switch_with_description, this)
        title = findViewById(R.id.titleText)
        content = findViewById(R.id.contentText)
        switch = findViewById(R.id.cwSwitch)

        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.CardViewWithSwitch)

        val titleText = typedArray.getString(R.styleable.CustomMaterialCardView_titleText)

        val contentText = typedArray.getString(R.styleable.CustomMaterialCardView_contentText)

        title.text = titleText

        content.text = contentText

        switch.setOnCheckedChangeListener { _, isChecked ->
            switchStateChangeListener?.onSwitchStateChanged(isChecked)
        }

        typedArray.recycle()
    }

    fun setOnSwitchStateChangeListener(listener: OnSwitchStateChangeListener) {
        switchStateChangeListener = listener
    }

    interface OnSwitchStateChangeListener {
        fun onSwitchStateChanged(isChecked: Boolean)
    }
}