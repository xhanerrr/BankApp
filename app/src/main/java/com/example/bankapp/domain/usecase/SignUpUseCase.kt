package com.example.bankapp.domain.usecase

import com.example.bankapp.domain.model.AuthData
import com.example.bankapp.domain.model.User
import com.example.bankapp.domain.repository.AuthRepository
import com.example.bankapp.domain.repository.UserRepository
import javax.inject.Inject

class SignUpUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(
        name: String,
        email: String,
        password: String,
        income: Double,
        expenses: Double,
        profession: String
    ): Result<User> {

        return authRepository.registerUser(AuthData(email, password))
            .map { user ->
                val updatedUser = user.copy(
                    name = name,
                    income = income,
                    expenses = expenses,
                    profession = profession
                )

                userRepository.saveUserProfile(updatedUser)

                updatedUser
            }
    }
}


