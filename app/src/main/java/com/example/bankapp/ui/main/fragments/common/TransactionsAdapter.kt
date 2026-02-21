package com.example.bankapp.ui.main.fragments.common

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.bankapp.databinding.ItemTransactionBinding
import com.example.bankapp.domain.model.Transaction
import com.example.bankapp.ui.main.fragments.home.addtransaction.TransactionActionListener
import java.util.Locale

class TransactionsAdapter(
    private val actionListener: TransactionActionListener? = null
) :
    ListAdapter<Transaction, TransactionsAdapter.TransactionViewHolder>(TransactionDiffCallback()) {

    inner class TransactionViewHolder(private val binding: ItemTransactionBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(transaction: Transaction) {

            binding.textDescription.text = transaction.description
            binding.textTransactionType.text = transaction.type

            val amountText = String.Companion.format(Locale.getDefault(), "$%.2f", transaction.amount)
            binding.textAmount.text = amountText

            binding.iconView.setImageResource(transaction.iconResId)

            if (actionListener != null) {
                binding.root.setOnClickListener {
                    actionListener.onEditTransaction(transaction)
                }
                binding.moreBtn.setOnClickListener {
                    actionListener.onDeleteTransactionClicked(it, transaction)
                }
            } else {
                binding.root.setOnClickListener(null)
                binding.moreBtn.setOnClickListener(null)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val binding = ItemTransactionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TransactionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    private class TransactionDiffCallback : DiffUtil.ItemCallback<Transaction>() {
        override fun areItemsTheSame(oldItem: Transaction, newItem: Transaction): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Transaction, newItem: Transaction): Boolean {
            return oldItem == newItem
        }
    }
}