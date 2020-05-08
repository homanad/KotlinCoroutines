package com.hmman.whycoroutines

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    private var count = 0;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        implementCoroutines()
        workOnMainThread()

    }

    private fun workOnMainThread() {
        download_button.setOnClickListener {
            downloadData()
        }

        add_button.setOnClickListener {
            count_text.text = count++.toString()
        }
    }

    private fun implementCoroutines() {
        download_button.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                downloadData()
            }
        }

        add_button.setOnClickListener {
            count_text.text = count++.toString()
        }
    }

    private fun downloadData() {
        for (i in 1..200000000) {
            Log.i(
                "WhyCoroutines?",
                "Download in progress: $i in 200000, on ${Thread.currentThread().name} thread"
            )
        }
    }
}
