package com.example.bankapp.domain.usecase

import com.example.bankapp.domain.model.User
import com.example.bankapp.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetUserDataUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    operator fun invoke(userId: String): Flow<Result<User>> {
        return userRepository.getUserProfile(userId)
    }
}