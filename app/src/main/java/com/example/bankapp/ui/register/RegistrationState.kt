package com.example.bankapp.ui.register

import com.example.bankapp.domain.model.ValidationResult

sealed class RegistrationState {
    object Idle : RegistrationState()
    object Loading : RegistrationState()

    data class Success(val userId: String) : RegistrationState()

    data class Error(val errorType: String) : RegistrationState()

    data class ValidationErrors(val errors: Map<String, ValidationResult>) : RegistrationState()
}
