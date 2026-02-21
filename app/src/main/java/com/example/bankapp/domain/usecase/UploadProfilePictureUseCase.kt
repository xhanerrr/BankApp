package com.example.bankapp.domain.usecase

import android.net.Uri
import com.example.bankapp.domain.repository.UserRepository
import javax.inject.Inject

class UploadProfilePictureUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(uid: String, imageUri: Uri) =
        userRepository.uploadProfilePicture(uid, imageUri)
}
