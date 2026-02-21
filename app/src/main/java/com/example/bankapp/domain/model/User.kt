package com.example.bankapp.domain.model

data class User(
    val uid: String,
    val name: String,
    val email: String,
    val income: Double,
    val expenses: Double,
    val profession: String,
    val profileImageUrl: String? = null
) {
    val netBalance: Double
        get() = income - expenses
}
