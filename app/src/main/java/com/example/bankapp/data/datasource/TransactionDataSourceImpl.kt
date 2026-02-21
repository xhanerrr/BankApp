package com.example.bankapp.data.datasource

import com.example.bankapp.data.model.TransactionDto
import com.example.bankapp.domain.datasource.TransactionDataSource
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class TransactionDataSourceImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
) : TransactionDataSource {

    companion object {
        private const val TRANSACTIONS_COLLECTION = "transactions"
        private const val USERS_COLLECTION = "users"
    }

    private fun getUserTransactionsCollection(userId: String) = firestore.collection(USERS_COLLECTION)
        .document(userId)
        .collection(TRANSACTIONS_COLLECTION)

    override suspend fun saveTransaction(userId: String, transactionDto: TransactionDto): Result<Unit> = try {
        getUserTransactionsCollection(userId)
            .add(transactionDto)
            .await()

        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override fun getTransactions(userId: String): Flow<Result<List<TransactionDto>>> =
        callbackFlow {

            val query = getUserTransactionsCollection(userId)
                .orderBy("date", Query.Direction.DESCENDING)

            val subscription = query.addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(Result.failure(error))
                    close(error)
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val transactionDtos = snapshot.documents.mapNotNull { doc ->
                        doc.toObject(TransactionDto::class.java)
                    }
                    trySend(Result.success(transactionDtos))
                } else {
                    trySend(Result.success(emptyList()))
                }
            }

            awaitClose { subscription.remove() }
        }

    override suspend fun updateTransaction(userId: String, transactionDto: TransactionDto): Result<Unit> = try {
        val transactionId = transactionDto.id
            ?: throw IllegalArgumentException("Transaction ID must not be null for update operation.")

        getUserTransactionsCollection(userId)
            .document(transactionId)
            .set(transactionDto)
            .await()

        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun deleteTransaction(userId: String, transactionDto: TransactionDto): Result<Unit> = try {
        val transactionId = transactionDto.id
            ?: throw IllegalArgumentException("Transaction ID must not be null for delete operation.")

        getUserTransactionsCollection(userId)
            .document(transactionId)
            .delete()
            .await()

        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }
}