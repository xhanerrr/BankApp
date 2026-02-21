package com.example.bankapp.domain.usecase

import com.example.bankapp.domain.model.ValidationResult
import javax.inject.Inject

class ValidateRegistrationFieldsUseCase @Inject constructor() {

    private val emailRegex = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}\$".toRegex()

    operator fun invoke(
        name: String,
        email: String,
        password: String,
        income: String,
        expenses: String,
        profession: String
    ): Map<String, ValidationResult> {
        val results = mutableMapOf<String, ValidationResult>()

        val trimmedName = name.trim()
        val trimmedEmail = email.trim()
        val trimmedPassword = password.trim()
        val trimmedIncome = income.trim()
        val trimmedExpenses = expenses.trim()
        val trimmedProfession = profession.trim()

        if (trimmedName.isBlank() || trimmedName.length < 3) {
            results["name"] = ValidationResult.Invalid("El nombre debe tener al menos 3 caracteres.")
        } else {
            results["name"] = ValidationResult.Valid
        }

        if (!trimmedEmail.matches(emailRegex)) {
            results["email"] = ValidationResult.Invalid("Formato de email inválido.")
        } else {
            results["email"] = ValidationResult.Valid
        }

        if (trimmedPassword.length < 6 || !trimmedPassword.contains(Regex("[A-Z]")) || !trimmedPassword.contains(Regex("[0-9]"))) {
            results["password"] = ValidationResult.Invalid("La contraseña debe tener al menos 6 caracteres, incluir 1 mayúscula y 1 número.")
        } else {
            results["password"] = ValidationResult.Valid
        }

        val incomeValue = trimmedIncome.toDoubleOrNull()
        val expensesValue = trimmedExpenses.toDoubleOrNull()

        if (incomeValue == null || incomeValue < 0) {
            results["income"] = ValidationResult.Invalid("Ingreso inválido.")
        } else {
            results["income"] = ValidationResult.Valid
        }

        if (expensesValue == null || expensesValue < 0) {
            results["expenses"] = ValidationResult.Invalid("Gasto inválido.")
        } else {
            results["expenses"] = ValidationResult.Valid
        }

        if (profession.isBlank()) {
            results["profession"] = ValidationResult.Invalid("Obligatorio.")
        } else {
            results["profession"] = ValidationResult.Valid
        }

        return results
    }
}