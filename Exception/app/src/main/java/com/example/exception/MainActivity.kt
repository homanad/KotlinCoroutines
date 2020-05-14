package com.example.exception

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import kotlinx.coroutines.*
import java.lang.ArithmeticException
import java.lang.IndexOutOfBoundsException

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        throwException1()
//        throwException2()
//        throwExceptionLaunch()
//        throwExceptionAsync()

        coroutineExceptionHandler()
    }

    private fun coroutineExceptionHandler() = runBlocking {
        val handler = CoroutineExceptionHandler { _, exception ->
            Log.i("Mytag", "Caught: $exception")
        }

        //launch
//        CoroutineScope(Dispatchers.IO).launch(handler) {
//            Log.i("MyTag", "Throwing exception from async")
//            throw ArithmeticException()
//            Log.i("MyTag", "Unreached")
//        }

        //async
        val deferred = CoroutineScope(Dispatchers.IO).async(handler) {
            Log.i("MyTag", "Throwing exception from async")
            throw ArithmeticException()
            Log.i("MyTag", "Unreached")
        }
        deferred.await()
    }

    private fun throwException2() = runBlocking {
        val deferred = GlobalScope.async {
            Log.i("MyTag", "Throwing exception from async")
            throw ArithmeticException()
            Log.i("MyTag", "Unreached")
        }
//        deferred.await()
    }

    private fun throwException1() = runBlocking {
        GlobalScope.launch {
            Log.i("MyTag", "Throwing exception from launch")
            throw IndexOutOfBoundsException()
            Log.i("Mytag", "Unreached!")
        }
    }

    fun throwExceptionLaunch() = runBlocking {
        GlobalScope.launch {
            try {
                Log.i("MyTag", "Throwing exception from launch")
                throw IndexOutOfBoundsException()
                Log.i("Mytag", "Unreached!")
            } catch (e: IndexOutOfBoundsException) {
                Log.i("Mytag", "Caught IndexOutOfBoundsException")
            }
        }
    }

    fun throwExceptionAsync() = runBlocking {
        val deferred = GlobalScope.async {
            Log.i("MyTag", "Throwing exception from async")
            throw ArithmeticException()
            Log.i("MyTag", "Unreached")

        }

        try {
            deferred.await()
        } catch (e: ArithmeticException) {
            Log.i("Mytag", "Caught IndexOutOfBoundsException")
        }
    }
}