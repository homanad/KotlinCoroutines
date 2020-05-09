package com.hmman.switchcoroutinethread

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.hmman.switchcoroutinethread.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var count = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        binding.apply {
            downloadButton.setOnClickListener {
                CoroutineScope(Dispatchers.IO).launch {
                    downloadAndSwitchThread()
                }
            }

            countButton.setOnClickListener {
                countTxt.text = count++.toString()
            }
        }
    }

    private suspend fun downloadAndSwitchThread() {
        for (i in 1..1000000) {
            withContext(Dispatchers.Main) {
                binding.downloadTxt.text =
                    "Download in progress: $i in ${Thread.currentThread().name} thread"
            }
        }
    }
}
