package com.example.bankapp.domain.datasource

import com.example.bankapp.data.model.UserDto
import kotlinx.coroutines.flow.Flow

interface UserDataSource {
    suspend fun saveUserProfile(userDto: UserDto)
    fun getUserProfileFlow(uid: String): Flow<Result<UserDto?>>
    suspend fun updateUserProfileField(uid: String, updates: Map<String, Any>): Result<Unit>
}