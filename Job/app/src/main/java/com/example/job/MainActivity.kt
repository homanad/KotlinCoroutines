package com.example.job

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import kotlinx.coroutines.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        manageWithJob1()
        manageWithJob2()
    }

    private fun manageWithJob2() = runBlocking {
        val job = CoroutineScope(Dispatchers.IO).async {
            async {
                for (i in 1..2000000) {
                    if (isActive)
                        Log.d("MyTag", "$i, Coroutine 1 in ${Thread.currentThread().name}")
                }
                Log.d("MyTag", "Coroutine 1 is completed or canceled")
            }

            async {
                for (i in 1..2000000) {
                    if (isActive)
                        Log.d("MyTag", "$i, Coroutine 2 in ${Thread.currentThread().name}")
                }
                Log.d("MyTag", "Coroutine 2 is completed  or canceled")
            }
        }

        delay(300)
        job.cancel()
    }

    private fun manageWithJob1() = runBlocking {
        val job = Job()

        CoroutineScope(Dispatchers.IO.plus(job)).async {
            for (i in 1..2000000) {
                if (isActive)
                    Log.d("MyTag", "$i, Coroutine 1 in ${Thread.currentThread().name}")

            }
            Log.d("MyTag", "Coroutine 1 is completed or canceled")
        }

        CoroutineScope(Dispatchers.IO.plus(job)).async {
            for (i in 1..2000000) {
                if (isActive)
                    Log.d("MyTag", "$i, Coroutine 2 in ${Thread.currentThread().name}")

            }
            Log.d("MyTag", "Coroutine 2 is completed or canceled")
        }

        delay(1000)
        job.cancel()
    }
}
