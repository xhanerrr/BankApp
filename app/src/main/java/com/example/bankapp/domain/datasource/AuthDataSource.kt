package com.example.bankapp.domain.datasource

import com.example.bankapp.domain.model.AuthData

interface AuthDataSource {

    suspend fun registerUser(authData: AuthData): Result<String>

    suspend fun loginUser(authData: AuthData): Result<String>

    fun getCurrentUserUid(): String?

    fun logout()
}
