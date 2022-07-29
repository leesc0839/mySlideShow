package com.example.myslideshow

import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.myslideshow.databinding.ActivitySlideshowBinding
import java.util.*
import kotlin.concurrent.timer

class SlideshowActivity : AppCompatActivity() {
    private val period: Long = 3000
    private var currentIdxTemp = 0
    private var timer: Timer? = null

    private lateinit var binding: ActivitySlideshowBinding
    private var mutableUriList: MutableList<Uri> = mutableListOf<Uri>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySlideshowBinding.inflate(layoutInflater)
        setContentView(binding.root)
        getUriFromIntent()
    }

    override fun onStart() {
        super.onStart()
        startTimer()
    }

    override fun onStop() {
        super.onStop()
        timer?.let {
            it.cancel()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        timer?.let {
            it.cancel()
        }
    }

    private fun getUriFromIntent() {
        val uriListSize = intent.getIntExtra("imgUriListSize", 0)
        for (i in 0..uriListSize) {
            intent.getStringExtra("imageView$i")?.let {
                mutableUriList.add(Uri.parse(it))
            }
        }
    }

    private fun startTimer() {
        timer = timer(period = period) {
            runOnUiThread {
                val currentIdx = currentIdxTemp
                val nextIdx =
                    if (mutableUriList.size <= currentIdxTemp + 1) 0 else currentIdxTemp + 1
                binding.backImageView.setImageURI(mutableUriList[currentIdx])
                binding.frontImageView.alpha = 0f
                binding.frontImageView.setImageURI(mutableUriList[nextIdx])
                binding.frontImageView.animate()
                    .alpha(1.0f)
                    .setDuration(2000)
                    .start()
                currentIdxTemp = nextIdx
            }
        }
    }
}