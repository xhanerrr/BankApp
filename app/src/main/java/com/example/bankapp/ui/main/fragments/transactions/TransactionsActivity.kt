package com.example.bankapp.ui.main.fragments.transactions

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bankapp.R
import com.example.bankapp.databinding.ActivityTransactionsBinding
import com.example.bankapp.domain.model.Transaction
import com.example.bankapp.ui.main.fragments.home.addtransaction.TransactionActionListener
import com.example.bankapp.ui.main.fragments.common.TransactionsAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class TransactionsActivity : AppCompatActivity(), TransactionActionListener {

    private val viewModel: TransactionsViewModel by viewModels()
    private lateinit var binding: ActivityTransactionsBinding
    private lateinit var transactionsAdapter: TransactionsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityTransactionsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        enableEdgeToEdge()

        setupRecyclerView()
        setupListeners()
        observeTransactions()

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun setupRecyclerView() {
        transactionsAdapter = TransactionsAdapter(this)
        binding.transactionsRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@TransactionsActivity)
            adapter = transactionsAdapter
        }
    }

    private fun setupListeners() {
        binding.backBtn.setOnClickListener {
            finish()
        }

        binding.btnAll.setOnClickListener {
            viewModel.setFilter(FilterType.ALL)
            updateButtonSelection(FilterType.ALL)
        }
        binding.btnIncome.setOnClickListener {
            viewModel.setFilter(FilterType.INCOME)
            updateButtonSelection(FilterType.INCOME)
        }
        binding.btnExpense.setOnClickListener {
            viewModel.setFilter(FilterType.EXPENSE)
            updateButtonSelection(FilterType.EXPENSE)
        }

        updateButtonSelection(viewModel.currentFilter.value)
    }

    private fun updateButtonSelection(selectedType: FilterType) {
        val allButtons = listOf(binding.btnAll, binding.btnIncome, binding.btnExpense)

        allButtons.forEach { button ->
            val isSelected = when (button.id) {
                R.id.btnAll -> selectedType == FilterType.ALL
                R.id.btnIncome -> selectedType == FilterType.INCOME
                R.id.btnExpense -> selectedType == FilterType.EXPENSE
                else -> false
            }

            if (isSelected) {
                button.setBackgroundResource(R.drawable.oval_button_selected)
                button.setTextColor(resources.getColor(R.color.white, null))
            } else {
                button.setBackgroundResource(R.drawable.oval_button)
                button.setTextColor(resources.getColor(R.color.black, null))
            }
        }
    }

    private fun observeTransactions() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.allFilteredTransactions.collect { transactions ->
                    transactionsAdapter.submitList(transactions)

                    val isLoading = false

                    binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
                    binding.transactionsRecyclerView.visibility = if (!isLoading && transactions.isNotEmpty()) View.VISIBLE else View.GONE
                    binding.textEmptyState.visibility = if (!isLoading && transactions.isEmpty()) View.VISIBLE else View.GONE
                }
            }
        }
    }

    override fun onEditTransaction(transaction: Transaction) {
    }

    override fun onDeleteTransactionClicked(
        view: View,
        transaction: Transaction
    ) {
    }

}