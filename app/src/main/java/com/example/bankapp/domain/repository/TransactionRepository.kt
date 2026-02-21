package com.example.bankapp.domain.repository

import com.example.bankapp.domain.model.Transaction
import kotlinx.coroutines.flow.Flow

interface TransactionRepository {

    suspend fun saveTransaction(transaction: Transaction): Result<Unit>

    fun getTransactions(userId: String): Flow<Result<List<Transaction>>>

    suspend fun updateTransaction(transaction: Transaction): Result<Unit>

    suspend fun deleteTransaction(transaction: Transaction): Result<Unit>
}