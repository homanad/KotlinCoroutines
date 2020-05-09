package com.hmman.structuredconcurrency

import kotlinx.coroutines.*

class UserDataManager2 {

    var count = 0
    lateinit var deferred: Deferred<Int>

    suspend fun getTotalUserCount(): Int {

        coroutineScope {
            launch(Dispatchers.IO) {
                //Ở đây nếu chúng ta không thêm một Dispatcher,
                // coroutine này sẽ chạy trong dispatcher của parent (trong trường hợp này là Dispatcher.Main)
                // Do đó ta sẽ add Dispatcher.IO
                delay(1000)
                count = 50
            }

            deferred = async(Dispatchers.IO) {
                delay(3000)
                return@async 70
            }
        }

        return count + deferred.await()
    }

}