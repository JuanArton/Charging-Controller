package com.juanarton.core.customview.customcardview

import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.google.android.material.card.MaterialCardView
import com.juanarton.core.R

class CustomMaterialCardView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : MaterialCardView(context, attrs, defStyleAttr) {

    private val icon: ImageView
    private val title: TextView
    private val content: TextView
    private val unit: TextView
    private val extra: TextView

    init {
        inflate(context, R.layout.custom_card_view, this)
        icon = findViewById(R.id.icon)
        title = findViewById(R.id.titleText)
        content = findViewById(R.id.contentText)
        unit = findViewById(R.id.unitText)
        extra = findViewById(R.id.extraText)

        val colorTypedValue = TypedValue()
        context.theme.resolveAttribute(android.R.attr.textColor, colorTypedValue, true)

        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.CustomMaterialCardView)

        val iconId = typedArray.getResourceId(R.styleable.CustomMaterialCardView_iconSrc, 0)
        val iconDescription = typedArray.getString(R.styleable.CustomMaterialCardView_iconDescription)
        val iconRotation = typedArray.getFloat(R.styleable.CustomMaterialCardView_iconRotation, 0F)

        val titleText = typedArray.getString(R.styleable.CustomMaterialCardView_titleText)

        val contentText = typedArray.getString(R.styleable.CustomMaterialCardView_contentText)
        val contentHint = typedArray.getString(R.styleable.CustomMaterialCardView_contentHint)
        val contentMarginTop = typedArray.getDimensionPixelSize(
            R.styleable.CustomMaterialCardView_contentMarginTop,
            resources.getDimensionPixelSize(R.dimen.default_content_margin_top)
        )
        val contentTextColor = typedArray.getColor(
            R.styleable.CustomMaterialCardView_contentTextColor,
            colorTypedValue.data
        )

        val extraText = typedArray.getString(R.styleable.CustomMaterialCardView_extraText)

        val unitText = typedArray.getString(R.styleable.CustomMaterialCardView_contentUnit)

        icon.setImageResource(iconId)
        icon.contentDescription = iconDescription
        icon.rotation = iconRotation

        title.text = titleText

        val layoutParams = content.layoutParams as MarginLayoutParams
        layoutParams.topMargin = contentMarginTop

        content.text = contentText
        content.hint = contentHint
        content.setTextColor(contentTextColor)
        content.layoutParams = layoutParams

        unit.text = unitText
        unit.layoutParams = layoutParams
        unit.setTextColor(contentTextColor)

        extra.text = extraText

        typedArray.recycle()
    }

    var titleText: CharSequence
        get() = title.text
        set(value) {
            title.text = value
        }

    var contentText: CharSequence
        get() = content.text
        set(value) {
            content.text = value
        }

    var iconResource: Int
        get() = icon.tag as Int
        set(value) {
            icon.setImageResource(value)
            icon.tag = value
        }

    var extraText: CharSequence
        get() = extra.text
        set(value) {
            extra.text = value
            val layoutParamsHeight = extra.layoutParams
            layoutParamsHeight.height = ViewGroup.LayoutParams.WRAP_CONTENT
            extra.layoutParams = layoutParamsHeight
        }
}

