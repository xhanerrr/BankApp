package com.example.bankapp.data.model

import com.google.firebase.firestore.PropertyName
import com.google.firebase.firestore.DocumentId

data class UserDto(
    @DocumentId var uid: String? = null,
    @get:PropertyName("name") @set:PropertyName("name") var name: String = "",
    @get:PropertyName("email") @set:PropertyName("email") var email: String = "",
    @get:PropertyName("income") @set:PropertyName("income") var income: Double = 0.0,
    @get:PropertyName("expenses") @set:PropertyName("expenses") var expenses: Double = 0.0,
    @get:PropertyName("profession") @set:PropertyName("profession") var profession: String = "",
    @get:PropertyName("profileImageUrl") @set:PropertyName("profileImageUrl") var profileImageUrl: String? = null
)