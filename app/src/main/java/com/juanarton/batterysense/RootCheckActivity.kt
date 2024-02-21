package com.juanarton.batterysense

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.juanarton.batterysense.databinding.ActivityRootCheckBinding


class RootCheckActivity : AppCompatActivity() {

    private var _binding: ActivityRootCheckBinding? = null
    private val binding get() = _binding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityRootCheckBinding.inflate(layoutInflater)
        setContentView(binding?.root)

    }
}