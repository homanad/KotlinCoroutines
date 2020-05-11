package com.hmman.viewmodelscope

import kotlinx.coroutines.delay

class UserRepository {

    suspend fun getUsers(): List<User> {
        delay(8000)
        return listOf(
            User(1, "hjkhjk"),
            User(2, "hjkhjk"),
            User(3, "hjkhjk"),
            User(4, "hjkhjk"),
            User(5, "hjkhjk"),
            User(6, "hjkhjk"),
            User(7, "hjkhjk"),
            User(8, "hjkhjk"),
            User(9, "hjkhjk"),
            User(10, "hjkhjk"),
            User(11, "hjkhjk")
        )
    }

}