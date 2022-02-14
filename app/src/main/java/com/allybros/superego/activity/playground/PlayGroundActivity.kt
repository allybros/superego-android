package com.allybros.superego.activity.playground

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import com.allybros.superego.R
import com.allybros.superego.databinding.ActivityPlayGroundBinding
import java.lang.Exception

class PlayGroundActivity : AppCompatActivity() {
    private val viewModel: PlayGroundVM by viewModels()
    lateinit var binding: ActivityPlayGroundBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_play_ground)
        binding.bt.setOnClickListener {
            val startPercent = binding.etStartPercent.text.toString()
            val endPercent = binding.etEndPercent.text.toString()
            var intStartPercent: Int
            var intEndPercent: Int
            try {
                intStartPercent = startPercent.toInt()
                intEndPercent = endPercent.toInt()
            } catch (e: Exception) {
                intStartPercent = 1
                intEndPercent = 99
            }
            binding.segoProgress.setNewPercent(intStartPercent, intEndPercent)
        }
    }
}