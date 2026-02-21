package com.example.bankapp.domain.model

sealed class AuthState {
    data class Success(val userId: String) : AuthState()
    data class Error(val message: String) : AuthState()
}