package com.example.bankapp.data.datasource

import com.example.bankapp.domain.datasource.AuthDataSource
import com.example.bankapp.domain.model.AuthData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthDataSourceImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) : AuthDataSource {

    override suspend fun registerUser(authData: AuthData): Result<String> {
        return try {
            val result = firebaseAuth.createUserWithEmailAndPassword(
                authData.email,
                authData.password
            ).await()

            val uid = result.user?.uid
                ?: return Result.failure(Exception("UID no disponible."))

            Result.success(uid)

        } catch (e: Exception) {
            val message = when (e) {
                is FirebaseAuthWeakPasswordException -> "Contraseña débil."
                is FirebaseAuthInvalidCredentialsException -> "Email inválido."
                is FirebaseAuthUserCollisionException -> "El email ya está registrado."
                else -> "Error al registrar: ${e.message}"
            }
            Result.failure(Exception(message))
        }
    }

    override suspend fun loginUser(authData: AuthData): Result<String> {
        return try {
            val result = firebaseAuth.signInWithEmailAndPassword(
                authData.email,
                authData.password
            ).await()

            val uid = result.user?.uid
                ?: return Result.failure(Exception("UID no disponible."))

            Result.success(uid)

        } catch (e: Exception) {
            val message = when (e) {
                is FirebaseAuthInvalidCredentialsException,
                is FirebaseAuthInvalidUserException -> "Correo o contraseña incorrectos."
                else -> "Error desconocido: ${e.message}"
            }
            Result.failure(Exception(message))
        }
    }

    override fun getCurrentUserUid(): String? {
        return firebaseAuth.currentUser?.uid
    }

    override fun logout() {
        firebaseAuth.signOut()
    }
}
