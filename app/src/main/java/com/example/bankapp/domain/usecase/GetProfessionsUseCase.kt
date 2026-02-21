package com.example.bankapp.domain.usecase

import com.example.bankapp.domain.model.Profession
import com.example.bankapp.domain.repository.UserRepository
import javax.inject.Inject

class GetProfessionsUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(): List<Profession> {
        return userRepository.getAvailableProfessions()
    }
}
