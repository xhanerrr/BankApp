package com.example.bankapp.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.Date

@Parcelize
data class Transaction(
    val id: String? = null,
    val userId: String? = null,
    val type: String,
    val description: String,
    val amount: Double,
    val date: Date = Date(),

    val iconResId: Int = 0
) : Parcelable