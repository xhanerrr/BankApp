package com.example.bankapp.domain.usecase

import com.example.bankapp.domain.repository.ProfileRepository
import javax.inject.Inject

class GetRandomProfileUrlsUseCase @Inject constructor(
    private val repository: ProfileRepository
) {
    suspend operator fun invoke(count: Int): Result<List<String>> = runCatching {
        val profileData = repository.getProfileUrls()

        if (profileData.urls.isEmpty()) {
            throw NoSuchElementException("La lista de URLs de perfil está vacía.")
        }

        return@runCatching profileData.urls.shuffled().take(count)
    }
}