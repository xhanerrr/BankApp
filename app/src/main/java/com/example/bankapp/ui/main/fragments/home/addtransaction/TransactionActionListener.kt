package com.example.bankapp.ui.main.fragments.home.addtransaction

import android.view.View
import com.example.bankapp.domain.model.Transaction

interface TransactionActionListener {
    fun onEditTransaction(transaction: Transaction)
    fun onDeleteTransactionClicked(view: View, transaction: Transaction)
}