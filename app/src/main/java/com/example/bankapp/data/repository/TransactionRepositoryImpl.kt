package com.example.bankapp.data.repository

import com.example.bankapp.domain.datasource.TransactionDataSource
import com.example.bankapp.data.mapper.toDto
import com.example.bankapp.data.mapper.toDomain
import com.example.bankapp.domain.model.Transaction
import com.example.bankapp.domain.repository.AuthRepository
import com.example.bankapp.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class TransactionRepositoryImpl @Inject constructor(
    private val transactionDataSource: TransactionDataSource,
    private val authRepository: AuthRepository
) : TransactionRepository {

    private fun getUserIdSafely(): String {
        return authRepository.getCurrentUserUid()
            ?: throw IllegalStateException("User must be logged in to perform transaction operations.")
    }

    override suspend fun saveTransaction(transaction: Transaction): Result<Unit> {
        val userId = getUserIdSafely()
        val transactionDto = transaction.toDto()

        val saveResult = transactionDataSource.saveTransaction(userId, transactionDto)

        return saveResult.map { Unit }
    }

    override fun getTransactions(userId: String): Flow<Result<List<Transaction>>> {
        return transactionDataSource.getTransactions(userId)
            .map { result ->
                result.map { dtoList ->
                    dtoList.map { it.toDomain() }
                }
            }
    }

    override suspend fun updateTransaction(transaction: Transaction): Result<Unit> {
        val userId = getUserIdSafely()
        val transactionDto = transaction.toDto()

        val updateResult = transactionDataSource.updateTransaction(userId, transactionDto)

        return updateResult.map { Unit }
    }

    override suspend fun deleteTransaction(transaction: Transaction): Result<Unit> {
        val userId = getUserIdSafely()
        val transactionDto = transaction.toDto()

        val deleteResult = transactionDataSource.deleteTransaction(userId, transactionDto)

        return deleteResult.map { Unit }
    }
}