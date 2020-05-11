package com.example.livedatabuilder

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainViewModel : ViewModel() {
    private var userRepository = UserRepository()

    var users = liveData(Dispatchers.IO) {
        val result = userRepository.getUsers()
        emit(result)
    }

//    var users = MutableLiveData<List<User>>()

//    fun getUsers() {
//        viewModelScope.launch {
//            var result: List<User>? = null
//            withContext(Dispatchers.IO) {
//                result = userRepository.getUsers()
//            }
//            users.value = result
//        }
//    }



}