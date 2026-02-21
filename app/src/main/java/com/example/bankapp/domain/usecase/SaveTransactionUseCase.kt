package com.example.bankapp.domain.usecase

import com.example.bankapp.domain.model.Transaction
import com.example.bankapp.domain.repository.TransactionRepository
import com.google.firebase.auth.FirebaseAuth
import javax.inject.Inject

class SaveTransactionUseCase @Inject constructor(
    private val transactionRepository: TransactionRepository,
    private val firebaseAuth: FirebaseAuth
) {

    suspend operator fun invoke(rawTransaction: Transaction): Result<Unit> {

        val userId = firebaseAuth.currentUser?.uid

        if (userId.isNullOrEmpty()) {
            return Result.failure(Exception("Usuario no autenticado. No se puede guardar la transacción."))
        }

        val transactionToSave = rawTransaction.copy(userId = userId)

        return transactionRepository.saveTransaction(transactionToSave)
    }
}