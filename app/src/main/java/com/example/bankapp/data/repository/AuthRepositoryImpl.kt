package com.example.bankapp.data.repository

import com.example.bankapp.domain.datasource.AuthDataSource
import com.example.bankapp.domain.model.AuthData
import com.example.bankapp.domain.model.User
import com.example.bankapp.domain.repository.AuthRepository
import com.example.bankapp.domain.repository.UserRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val authDataSource: AuthDataSource,
    private val userRepository: UserRepository
) : AuthRepository {

    override suspend fun registerUser(authData: AuthData): Result<User> {
        val authResult = authDataSource.registerUser(authData)

        return authResult.mapCatching { uid ->

            val user = User(
                uid = uid,
                name = "",
                email = authData.email,
                income = 0.0,
                expenses = 0.0,
                profession = ""
            )

            userRepository.saveUserProfile(user)

            user
        }
    }

    override suspend fun loginUser(authData: AuthData): Result<User> {
        val result = authDataSource.loginUser(authData)

        return result.mapCatching { uid ->
            userRepository.getUserProfile(uid).first().getOrThrow()
        }
    }

    override fun getCurrentUserUid(): String? =
        authDataSource.getCurrentUserUid()

    override fun logout() {
        authDataSource.logout()
    }
}
