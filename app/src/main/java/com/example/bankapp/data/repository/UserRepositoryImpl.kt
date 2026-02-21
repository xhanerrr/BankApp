package com.example.bankapp.data.repository

import android.content.Context
import android.net.Uri
import com.example.bankapp.domain.datasource.UserDataSource
import com.example.bankapp.data.model.UserDto
import com.example.bankapp.domain.model.Profession
import com.example.bankapp.domain.model.User
import com.example.bankapp.domain.repository.UserRepository
import com.google.common.reflect.TypeToken
import com.google.firebase.storage.FirebaseStorage
import com.google.gson.Gson
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val userDataSource: UserDataSource,
    private val firebaseStorage: FirebaseStorage,
    @ApplicationContext private val context: Context
) : UserRepository {

    private fun User.toDto(): UserDto = UserDto(
        uid = uid,
        name = name,
        email = email,
        income = income,
        expenses = expenses,
        profession = profession,
        profileImageUrl = profileImageUrl
    )

    private fun UserDto.toDomain(): User = User(
        uid = uid ?: throw IllegalStateException("UID es nulo en UserDto"),
        name = name,
        email = email,
        income = income,
        expenses = expenses,
        profession = profession,
        profileImageUrl = profileImageUrl
    )


    override suspend fun saveUserProfile(user: User) {
        val userDto = user.toDto()
        userDataSource.saveUserProfile(userDto)
    }

    override fun getUserProfile(uid: String): Flow<Result<User>> {
        return userDataSource.getUserProfileFlow(uid)
            .map { result ->
                result.fold(
                    onSuccess = { userDto ->
                        if (userDto != null) {
                            Result.success(userDto.toDomain())
                        } else {
                            Result.failure(Exception("Perfil de usuario no encontrado en Firestore."))
                        }
                    },
                    onFailure = { e ->
                        Result.failure(e)
                    }
                )
            }
    }

    override suspend fun uploadProfilePicture(uid: String, imageUri: Uri): Result<Unit> {
        return try {
            val storageRef = firebaseStorage.reference.child("profile_pictures/$uid.jpg")

            storageRef.putFile(imageUri).await()

            val downloadUrl = storageRef.downloadUrl.await().toString()

            userDataSource.updateUserProfileField(uid, mapOf("profileImageUrl" to downloadUrl))

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getAvailableProfessions(): List<Profession> = withContext(Dispatchers.IO) {
        try {
            val jsonString = context.assets.open("professions.json")
                .bufferedReader()
                .use { it.readText() }

            val listType = object : TypeToken<List<String>>() {}.type
            val professionNames: List<String> = Gson().fromJson(jsonString, listType)

            professionNames.map { Profession(it) }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
}