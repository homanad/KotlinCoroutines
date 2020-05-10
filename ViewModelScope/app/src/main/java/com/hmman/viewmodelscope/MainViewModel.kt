package com.hmman.viewmodelscope

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {

    private val myJob = Job()
    private val myScope = CoroutineScope(Dispatchers.IO.plus(myJob))

    fun getUserData() {
        myScope.launch {

        }
    }

    override fun onCleared() {
        super.onCleared()
        myJob.cancel()
    }

}