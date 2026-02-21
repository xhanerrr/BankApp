package com.example.bankapp.domain.model

sealed class HomeUiState {
    data object Loading : HomeUiState()
    data class Success(
        val user: User,
        val recentTransactions: List<Transaction>
    )
        : HomeUiState()
    data class Error(val message: String) : HomeUiState()
}