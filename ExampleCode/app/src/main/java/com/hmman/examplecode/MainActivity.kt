package com.hmman.examplecode

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        testThread()
        testCoroutines()

    }

    fun testThread() {
        testThread1()
        testThread2()
    }

    private fun testThread1() {
        Log.i("MYTAG", "Test 1 is started!, ${Thread.currentThread().name} thread")
        Thread.sleep(500)
        Log.i("MYTAG", "Test 1 is ended!, ${Thread.currentThread().name} thread")
    }

    private fun testThread2() {
        Log.i("MYTAG", "Test 2 is started!, ${Thread.currentThread().name} thread")
        Thread.sleep(1000)
        Log.i("MYTAG", "Test 2 is ended!, ${Thread.currentThread().name} thread")
    }


    fun testCoroutines() = runBlocking {
        launch {
            testCoroutine1()
        }
        launch {
            testCotoutine2()
        }
    }

    suspend fun testCoroutine1() {
        Log.i("MYTAG", "Coroutine 1 is started!, ${Thread.currentThread().name} thread")
        delay(500)
        Log.i("MYTAG", "Coroutine 1 is ended!, ${Thread.currentThread().name} thread")
    }

    suspend fun testCotoutine2() {
        Log.i("MYTAG", "Coroutine 2 is started!, ${Thread.currentThread().name} thread")
        delay(1000)
        Log.i("MYTAG", "Coroutine 2 is ended!, ${Thread.currentThread().name} thread")
    }


}
