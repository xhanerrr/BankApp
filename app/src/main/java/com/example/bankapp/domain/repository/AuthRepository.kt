package com.example.bankapp.domain.repository

import com.example.bankapp.domain.model.AuthData
import com.example.bankapp.domain.model.User


interface AuthRepository {
    suspend fun registerUser(authData: AuthData): Result<User>
    suspend fun loginUser(authData: AuthData): Result<User>
    fun getCurrentUserUid(): String?

    fun logout()

}


