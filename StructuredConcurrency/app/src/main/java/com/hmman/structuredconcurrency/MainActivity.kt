package com.hmman.structuredconcurrency

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btn_download.setOnClickListener {
            CoroutineScope(Dispatchers.Main).launch {
//                txt_download.text = UserDataManager1().getTotalUserCount().toString()
                txt_download.text = UserDataManager2().getTotalUserCount().toString()
            }
        }
    }

    private fun downloadUserData() {
        for (i in 1..200000) {
            Log.i(
                "StructuredConcurrency",
                "Download user $i in ${Thread.currentThread().name} thread"
            )
        }
    }
}
