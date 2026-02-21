package com.example.bankapp.domain.repository

import android.net.Uri
import com.example.bankapp.domain.model.Profession
import com.example.bankapp.domain.model.User
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    suspend fun saveUserProfile(user: User)
    fun getUserProfile(uid: String): Flow<Result<User>>
    suspend fun uploadProfilePicture(uid: String, imageUri: Uri): Result<Unit>
    suspend fun getAvailableProfessions(): List<Profession>
}
