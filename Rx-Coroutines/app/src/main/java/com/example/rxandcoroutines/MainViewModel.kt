package com.example.rxandcoroutines

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers

class MenuViewModel(
    private val menuRepository: MenuRepository
) : ViewModel() {
    val coffeeList: LiveData<List<Coffee>>
        get() = _coffeeList
    private val _coffeeList = MutableLiveData<List<Coffee>>()
    private val disposeBag = CompositeDisposable()

    override fun onCleared() {
        super.onCleared()
        disposeBag.dispose()
    }

    fun loadMenu() {
        val disposable = menuRepository.getMenu()
            .subscribe { list: List<Coffee> ->
                _coffeeList.value = list
            }
        disposeBag.add(disposable)
    }
}

class MenuRepository(
    private val menuApi: MenuApi
) {

    fun getMenu(): Single<List<Coffee>> {
        return menuApi.getMenu()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
    }
}

interface MenuApi {
    fun getMenu(): Single<List<Coffee>>
}