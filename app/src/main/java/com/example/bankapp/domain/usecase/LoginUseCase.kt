package com.example.bankapp.domain.usecase

import com.example.bankapp.domain.model.AuthData
import com.example.bankapp.domain.model.User
import com.example.bankapp.domain.repository.AuthRepository
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(
        email: String,
        password: String
    ): Result<User> {
        val authData = AuthData(email, password)
        return authRepository.loginUser(authData)
    }
}
