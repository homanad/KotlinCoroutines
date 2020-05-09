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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_main)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        binding.apply {
            downloadButton.setOnClickListener {
                downloadAndSwitchThread()
            }
        }
    }

    private fun downloadAndSwitchThread() {
        CoroutineScope(Dispatchers.IO).launch {
            for (i in 1..1000000) {
                withContext(Dispatchers.Main) {
                    binding.downloadTxt.text =
                        "Download in progress: $i in ${Thread.currentThread().name} thread"
                }
            }
        }
    }
}
