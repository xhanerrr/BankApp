package com.example.bankapp.domain.model

sealed class AddTransactionUiState {
    data object Idle : AddTransactionUiState()
    data object Loading : AddTransactionUiState()
    data object Success : AddTransactionUiState()
    data class Error(val message: String) : AddTransactionUiState()
}