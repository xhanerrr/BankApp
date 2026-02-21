package com.example.bankapp.data.mapper

import com.example.bankapp.R
import com.example.bankapp.data.model.TransactionDto
import com.example.bankapp.domain.model.Transaction

fun TransactionDto.toDomain(): Transaction {
    val isIncome = this.type.equals("Income", ignoreCase = true) || this.type.equals("Receive", ignoreCase = true)
    val iconResId = if (isIncome) R.drawable.decrease else R.drawable.increase

    return Transaction(
        id = this.id,
        userId = this.userId,
        type = this.type,
        description = this.description,
        amount = this.amount,
        iconResId = iconResId,
        date = this.date
    )
}

fun Transaction.toDto(): TransactionDto {
    return TransactionDto(
        userId = this.userId,
        type = this.type,
        description = this.description,
        amount = this.amount,
        date = this.date
    ).apply {
        id = this@toDto.id
    }
}
