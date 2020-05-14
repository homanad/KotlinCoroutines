package com.example.supervisorjob

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import kotlinx.coroutines.*
import java.lang.AssertionError

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        startSupervisorJob()
        startSupervisorScope()

    }

    private fun startSupervisorScope() = runBlocking {
        val handler = CoroutineExceptionHandler { _, exception ->
            Log.i("mytag", "Caught $exception")
        }
        supervisorScope {
            val first = launch(handler) {
                Log.i("mytag", "Child throws an exception")
                throw AssertionError()
            }
            val second = launch {
                delay(100)
                Log.i("Mytag", "Scope is completing")
            }
        }
        Log.i("mytag", "Scope is completed")
    }

    fun startSupervisorJob() = runBlocking {
        val supervisorJob = SupervisorJob()

        with(CoroutineScope(coroutineContext + supervisorJob)) {
            Log.i("mytag", "${Thread.currentThread().name} thread")

            val firstChild = launch(CoroutineExceptionHandler { _, _ -> }) {
                Log.i("mytag", "first child is failing")
                throw AssertionError("First child is cancelled")
            }

            val secondChild = launch {
                firstChild.join()
                Log.i("mytag", "First child is cancelled: ${firstChild.isCancelled}, but second child is still active")
                try {
                    delay(Long.MAX_VALUE)
                } finally {
                    Log.i("mytag", "Second child is cancelled because supervisor is cancelled")
                }
            }


            firstChild.join()
            Log.i("mytag", "Cancelling supervisor")
            supervisorJob.cancel()
            secondChild.join()
        }
    }
}
