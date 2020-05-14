package com.example.rxandcoroutines

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers

class MenuViewModel2(
    private val menuRepository: MenuRepository2
) : ViewModel() {

    val coffeeList: LiveData<List<Coffee>> =
        liveData(viewModelScope.coroutineContext + Dispatchers.IO) {
            emit(menuRepository.getMenu())
        }
}

class MenuRepository2(
    private val menuApi: MenuApi2
) {
    suspend fun getMenu(): List<Coffee> {
        return menuApi.getMenu()
    }
}

interface MenuApi2 {
    suspend fun getMenu(): List<Coffee>
}