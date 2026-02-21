package com.example.bankapp.ui.main.fragments.transactions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bankapp.domain.model.ProfileUiState
import com.example.bankapp.domain.model.Transaction
import com.example.bankapp.domain.repository.AuthRepository
import com.example.bankapp.domain.repository.TransactionRepository
import com.example.bankapp.domain.usecase.GetRandomProfileUrlsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class FilterType { ALL, INCOME, EXPENSE }

@HiltViewModel
class TransactionsViewModel @Inject constructor(
    authRepository: AuthRepository,
    private val getRandomProfileUrlsUseCase: GetRandomProfileUrlsUseCase,
    private val transactionRepository: TransactionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<ProfileUiState>(ProfileUiState.Loading)
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    private val userId = authRepository.getCurrentUserUid() ?: ""

    init {
        loadProfileAvatars()
    }

    private val _currentFilter = MutableStateFlow(FilterType.ALL)
    val currentFilter: StateFlow<FilterType> = _currentFilter

    val allFilteredTransactions: StateFlow<List<Transaction>> =
        transactionRepository.getTransactions(userId)
            .map { result -> result.getOrDefault(emptyList()) }
            .combine(_currentFilter) { transactions, filter ->
                when (filter) {
                    FilterType.ALL -> transactions
                    FilterType.INCOME -> transactions.filter {
                        it.type.equals("Income", ignoreCase = true) ||
                                it.type.equals("Receive", ignoreCase = true)
                    }
                    FilterType.EXPENSE -> transactions.filter {
                        it.type.equals("Expense", ignoreCase = true) ||
                                it.type.equals("Sent", ignoreCase = true)
                    }
                }
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

    fun loadProfileAvatars() {
        _uiState.value = ProfileUiState.Loading

        viewModelScope.launch {
            try {
                val urls = getRandomProfileUrlsUseCase(count = 5)
                    .getOrElse { throw it }

                if (urls.isNullOrEmpty()) {
                    _uiState.value = ProfileUiState.Error("La lista de perfiles está vacía")
                    return@launch
                }

                _uiState.value = ProfileUiState.Success(urls)

            } catch (e: Exception) {
                _uiState.value = ProfileUiState.Error(e.message ?: "Error al cargar perfiles")
            }
        }
    }

    val latestFilteredTransaction: StateFlow<List<Transaction>> =
        allFilteredTransactions
            .map { list -> list.take(1) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

    fun setFilter(filterType: FilterType) {
        _currentFilter.value = filterType
    }

    fun updateTransaction(transaction: Transaction) = viewModelScope.launch {
        transactionRepository.updateTransaction(transaction)
    }

    fun deleteTransaction(transaction: Transaction) = viewModelScope.launch {
        transactionRepository.deleteTransaction(transaction)
    }
}
