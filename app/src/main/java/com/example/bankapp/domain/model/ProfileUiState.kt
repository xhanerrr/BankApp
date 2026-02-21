package com.example.bankapp.domain.model

sealed interface ProfileUiState {
    data object Loading : ProfileUiState
    data class Success(val urls: List<String>) : ProfileUiState
    data class Error(val message: String) : ProfileUiState
}