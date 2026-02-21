package com.example.bankapp.ui.main.fragments.home.addtransaction

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.bankapp.databinding.FragmentAddTransactionDialogBinding
import com.example.bankapp.domain.model.AddTransactionUiState
import com.example.bankapp.domain.model.Transaction
import com.example.bankapp.ui.main.fragments.transactions.TransactionsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.Date

@AndroidEntryPoint
class AddTransactionDialogFragment : DialogFragment() {

    companion object {
        const val KEY_TRANSACTION = "key_transaction"
    }

    private var _binding: FragmentAddTransactionDialogBinding? = null
    private val binding get() = _binding!!

    private val transactionsViewModel: TransactionsViewModel by viewModels()
    private val viewModel: AddTransactionViewModel by viewModels()

    private var transactionToEdit: Transaction? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddTransactionDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.let {
            transactionToEdit = it.getParcelable(KEY_TRANSACTION)
            transactionToEdit?.let { transaction ->
                setupEditMode(transaction)
            }
        }

        viewModel.resetState()

        binding.btnSaveTransaction.setOnClickListener {
            saveNewTransaction()
        }

        observeViewModel()
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    when (state) {
                        AddTransactionUiState.Idle -> setUiEnabled(true)
                        AddTransactionUiState.Loading -> setUiEnabled(false)
                        is AddTransactionUiState.Success -> {
                            setUiEnabled(true)
                            val message = if (transactionToEdit != null) "Transacción actualizada con éxito!" else "Transacción guardada con éxito!"
                            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                            dismiss()
                            viewModel.resetState()
                        }
                        is AddTransactionUiState.Error -> {
                            setUiEnabled(true)
                            Toast.makeText(context, "Error: ${state.message}", Toast.LENGTH_LONG).show()
                            viewModel.resetState()
                        }
                    }
                }
            }
        }
    }

    private fun setupEditMode(transaction: Transaction) {
        binding.dialogTitle.text = "Editar transacción"

        binding.inputAmount.setText(transaction.amount.toString())
        binding.inputDescription.setText(transaction.description)

        if (transaction.type.equals("Income", ignoreCase = true) || transaction.type.equals("Receive", ignoreCase = true)) {
            binding.radioIncome.isChecked = true
        } else {
            binding.radioExpense.isChecked = true
        }

        binding.btnSaveTransaction.text = "Actualizar transacción"
    }


    private fun saveNewTransaction() {
        val amountText = binding.inputAmount.text.toString()
        val description = binding.inputDescription.text.toString()
        val isIncome = binding.radioIncome.isChecked

        if (amountText.isEmpty() || description.isEmpty()) {
            Toast.makeText(context, "Por favor, complete todos los campos.", Toast.LENGTH_SHORT).show()
            return
        }

        val amount = amountText.toDoubleOrNull()
        if (amount == null || amount <= 0) {
            Toast.makeText(context, "Monto inválido.", Toast.LENGTH_SHORT).show()
            return
        }

        val transactionType = if (isIncome) "Income" else "Expense"

        val finalTransaction = transactionToEdit?.copy(
            id = transactionToEdit?.id,
            userId = transactionToEdit?.userId ?: "",
            type = transactionType,
            description = description,
            amount = amount,
            date = transactionToEdit?.date ?: Date()
        ) ?: Transaction(
            id = null,
            userId = "",
            type = transactionType,
            description = description,
            amount = amount,
            date = Date()
        )

        viewModel.saveTransaction(finalTransaction, isEdit = transactionToEdit != null)

    }

    private fun setUiEnabled(enabled: Boolean) {
        binding.btnSaveTransaction.isEnabled = enabled
        binding.inputAmount.isEnabled = enabled
        binding.inputDescription.isEnabled = enabled
        binding.radioGroupType.isEnabled = enabled
        binding.progressBar.visibility = if (enabled) View.GONE else View.VISIBLE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}