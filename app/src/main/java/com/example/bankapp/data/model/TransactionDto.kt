package com.example.bankapp.data.model

import com.google.firebase.firestore.PropertyName
import com.google.firebase.firestore.DocumentId
import java.util.Date

data class TransactionDto(
    @get:PropertyName("user_id") @set:PropertyName("user_id") var userId: String? = null,

    @get:PropertyName("type") @set:PropertyName("type") var type: String = "",

    @get:PropertyName("description") @set:PropertyName("description") var description: String = "",

    @get:PropertyName("amount") @set:PropertyName("amount") var amount: Double = 0.0,

    @get:PropertyName("date") @set:PropertyName("date") var date: Date = Date()

) {
    @DocumentId var id: String? = null

    constructor() : this(type = "", description = "", amount = 0.0)
}