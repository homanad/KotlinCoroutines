package com.example.livedatabuilder

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
//        viewModel.getUsers()
        viewModel.users.observe(this, Observer { users ->
            users.forEach {
                Log.i("USERDATA", "User: ID - ${it.id}, NAME - ${it.name}")
            }
        })
    }
}
