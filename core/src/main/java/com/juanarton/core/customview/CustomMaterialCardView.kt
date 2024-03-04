package com.juanarton.core.customview

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.material.card.MaterialCardView
import com.juanarton.core.R

class CustomMaterialCardView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : MaterialCardView(context, attrs, defStyleAttr) {

    private val icon: ImageView
    private val head: TextView
    private val content: TextView

    init {

        inflate(context, R.layout.custom_card_view, this)
        icon = findViewById(R.id.icon)
        head = findViewById(R.id.titleText)
        content = findViewById(R.id.contentText)

        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.CustomMaterialCardView)
        val imageResId = typedArray.getResourceId(R.styleable.CustomMaterialCardView_iconSrc, 0)
        val headText = typedArray.getString(R.styleable.CustomMaterialCardView_titleText)
        val contentText = typedArray.getString(R.styleable.CustomMaterialCardView_contentText)

        icon.setImageResource(imageResId)
        head.text = headText
        content.text = contentText

        typedArray.recycle()
    }

    fun setIcon(resId: Int) {
        icon.setImageResource(resId)
    }

    fun setHead(text: CharSequence) {
        head.text = text
    }

    fun setContent(text: CharSequence) {
        content.text = text
    }
}
