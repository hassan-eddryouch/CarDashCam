package com.app.cardashcam.data.repository

import com.app.cardashcam.data.local.dao.UserDao
import com.app.cardashcam.data.local.entity.UserEntity

class AuthRepository(private val dao: UserDao) {

    suspend fun register(name: String, email: String, password: String): Boolean {

        val existing = dao.getByEmail(email)
        if (existing != null) return false

        dao.insert(
            UserEntity(
                name = name,
                email = email,
                password = password
            )
        )

        return true
    }

    suspend fun login(email: String, password: String): Boolean {

        val user = dao.getByEmail(email) ?: return false
        return user.password == password
    }
}
