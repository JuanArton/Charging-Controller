package com.juanarton.core.customview

import android.content.Context
import android.util.AttributeSet
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

    init {

        inflate(context, R.layout.custom_card_view, this)
        icon = findViewById(R.id.icon)
        title = findViewById(R.id.titleText)
        content = findViewById(R.id.contentText)
        unit = findViewById(R.id.unitText)

        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.CustomMaterialCardView)
        val iconId = typedArray.getResourceId(R.styleable.CustomMaterialCardView_iconSrc, 0)
        val titleText = typedArray.getString(R.styleable.CustomMaterialCardView_titleText)
        val contentText = typedArray.getString(R.styleable.CustomMaterialCardView_contentText)
        val contentHint = typedArray.getString(R.styleable.CustomMaterialCardView_contentHint)
        val unitText = typedArray.getString(R.styleable.CustomMaterialCardView_contentUnit)
        val iconDescription = typedArray.getString(R.styleable.CustomMaterialCardView_iconDescription)
        val iconRotation = typedArray.getFloat(R.styleable.CustomMaterialCardView_iconRotation, 0F)


        icon.setImageResource(iconId)
        icon.contentDescription = iconDescription
        icon.rotation = iconRotation
        title.text = titleText
        content.text = contentText
        content.hint = contentHint
        unit.text = unitText
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
}

