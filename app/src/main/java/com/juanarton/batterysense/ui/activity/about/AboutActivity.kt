package com.juanarton.batterysense.ui.activity.about

import android.content.Intent
import android.graphics.Typeface
import android.graphics.text.LineBreaker
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.animation.AnimationUtils
import android.widget.ArrayAdapter
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.juanarton.batterysense.R
import com.juanarton.batterysense.databinding.ActivityAboutBinding


class AboutActivity : AppCompatActivity() {

    private var _binding: ActivityAboutBinding? = null
    private val binding get() = _binding

    private val moduleUrl = "https://github.com/JuanArton/Charging-Controller/releases"
    private val repositoryUrl = "https://github.com/JuanArton/Charging-Controller"
    private val libList = listOf(
        "Lottie", "MPAndroidChart by philjay", "MultiWaveHeader by scwang90", "DotsIndicator by tommybuonomo",
        "ExpandableTextView by blogcat", "Room", "SQLCipher", "SQLite", "Hilt", "Libsu by topjohnwu"
    )


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        _binding = ActivityAboutBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding?.apply {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                expandableTextView.justificationMode = LineBreaker.JUSTIFICATION_MODE_INTER_WORD
            }

            val adapter = ArrayAdapter(this@AboutActivity, R.layout.lib_used_item, libList)
            libUsedList.adapter = adapter

            btToggle.setOnClickListener {
                if (expandableTextView.isExpanded) {
                    expandableTextView.text = getString(R.string.see_me)
                    expandableTextView.setTypeface(null, Typeface.BOLD)
                    startRotationAnimation(btToggle, true)
                    expandableTextView.collapse()
                } else {
                    expandableTextView.text = getString(R.string.app_desc)
                    expandableTextView.setTypeface(null, Typeface.NORMAL)
                    startRotationAnimation(btToggle, false)
                    expandableTextView.expand()
                }
            }

            ibDownload.setOnClickListener{
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(moduleUrl))
                startActivity(intent)
            }

            ibRepository.setOnClickListener{
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(repositoryUrl))
                startActivity(intent)
            }
        }
    }

    private fun startRotationAnimation(button: ImageButton, expanded: Boolean) {
        val rotateAnimation = if (expanded) {
            AnimationUtils.loadAnimation(this@AboutActivity, R.anim.button_rotation_up)
        }
        else {
            AnimationUtils.loadAnimation(this@AboutActivity, R.anim.button_rotation_down)
        }
        button.startAnimation(rotateAnimation)
    }
}