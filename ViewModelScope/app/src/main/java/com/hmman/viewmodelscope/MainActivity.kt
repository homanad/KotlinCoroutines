package com.hmman.viewmodelscope

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
//        viewModel.users.observe(this, Observer { users ->
//            users.forEach {
//                Log.i("USERDATA", "User: ID - ${it.id}, NAME - ${it.name}")
//            }
//        })
//
//        viewModel.getUserData()

        execute()
    }

    fun execute() = runBlocking {
        launch {
            test1WithCoroutines()
        }
        launch {
            test2WithCoroutines()
        }
    }

    suspend fun test1WithCoroutines() {
        delay(100)
        println("Start coroutines test 1: ${Thread.currentThread().name}")
        delay(20000)
        println("End coroutines test 1: ${Thread.currentThread().name}")
    }

    suspend fun test2WithCoroutines() {
        println("Start coroutines test 2: ${Thread.currentThread().name}")
        delay(1000)
        println("End coroutines test 2: ${Thread.currentThread().name}")
    }

}
