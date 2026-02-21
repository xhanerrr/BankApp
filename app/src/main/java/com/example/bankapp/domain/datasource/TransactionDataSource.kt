package com.example.bankapp.domain.datasource

import com.example.bankapp.data.model.TransactionDto
import kotlinx.coroutines.flow.Flow

interface TransactionDataSource {
    suspend fun saveTransaction(userId: String, transactionDto: TransactionDto): Result<Unit>

    fun getTransactions(userId: String): Flow<Result<List<TransactionDto>>>

    suspend fun updateTransaction(userId: String, transactionDto: TransactionDto): Result<Unit>

    suspend fun deleteTransaction(userId: String, transactionDto: TransactionDto): Result<Unit>
}