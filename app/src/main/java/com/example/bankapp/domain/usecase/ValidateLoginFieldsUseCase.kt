package com.example.bankapp.domain.usecase

import com.example.bankapp.domain.model.ValidationResult
import javax.inject.Inject

class ValidateLoginFieldsUseCase @Inject constructor() {

    private fun isEmailFormatValid(email: String): Boolean {
        return email.contains("@")
    }

    operator fun invoke(email: String, password: String): Map<String, ValidationResult> {

        val trimmedEmail = email.trim()
        val trimmedPassword = password.trim()
        val errors = mutableMapOf<String, ValidationResult>()

        if (trimmedEmail.isBlank()) {
            errors["email"] = ValidationResult.Invalid("El email es obligatorio.")
        } else if (!isEmailFormatValid(trimmedEmail)) {
            errors["email"] = ValidationResult.Invalid("Formato de email inválido.")
        }

        if (trimmedPassword.isBlank()) {
            errors["password"] = ValidationResult.Invalid("La contraseña es obligatoria.")
        }

        return errors.filterValues { it is ValidationResult.Invalid }
    }
}