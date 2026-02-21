package com.example.bankapp.domain.usecase

import com.example.bankapp.domain.model.Transaction
import com.example.bankapp.domain.repository.TransactionRepository
import javax.inject.Inject

class UpdateTransactionUseCase @Inject constructor(
    private val repository: TransactionRepository
) {
    suspend operator fun invoke(transaction: Transaction): Result<Unit> {
        return repository.updateTransaction(transaction)
    }
}
