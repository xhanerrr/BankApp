package com.example.bankapp.data.datasource

import com.example.bankapp.data.model.UserDto
import com.example.bankapp.domain.datasource.UserDataSource
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class UserDataSourceImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : UserDataSource {

    companion object {
        private const val USERS_COLLECTION = "users"
    }

    private fun usersCollection() = firestore.collection(USERS_COLLECTION)

    override suspend fun saveUserProfile(userDto: UserDto) {

        val uid: String = userDto.uid ?:
        throw IllegalArgumentException("Error de Robustez: UID es requerido para guardar el perfil de usuario.")

        usersCollection().document(uid).set(userDto).await()
    }

    override fun getUserProfileFlow(uid: String): Flow<Result<UserDto?>> = callbackFlow {
        val subscription = usersCollection().document(uid)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(Result.failure(error))
                    return@addSnapshotListener
                }

                if (snapshot != null && snapshot.exists()) {
                    val userDto = snapshot.toObject(UserDto::class.java)
                    trySend(Result.success(userDto))
                } else {
                    trySend(Result.success(null))
                }
            }

        awaitClose { subscription.remove() }
    }

    override suspend fun updateUserProfileField(uid: String, updates: Map<String, Any>): Result<Unit> = try {
        usersCollection().document(uid).update(updates).await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }
}