package com.example.livedatabuilder

import kotlinx.coroutines.delay

class UserRepository {

    suspend fun getUsers(): List<User> {
        delay(8000)
        return listOf(
            User(1, "HAHAHA"),
            User(2, "HAHAHA"),
            User(3, "HAHAHA"),
            User(4, "HAHAHA"),
            User(5, "HAHAHA"),
            User(6, "HAHAHA")
        )
    }
}