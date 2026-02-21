package com.example.bankapp.ui.main.fragments.home.addtransaction

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bankapp.domain.model.AddTransactionUiState
import com.example.bankapp.domain.model.Transaction
import com.example.bankapp.domain.usecase.SaveTransactionUseCase
import com.example.bankapp.domain.usecase.UpdateTransactionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddTransactionViewModel @Inject constructor(
    private val saveTransactionUseCase: SaveTransactionUseCase,
    private val updateTransactionUseCase: UpdateTransactionUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<AddTransactionUiState>(AddTransactionUiState.Idle)
    val uiState: StateFlow<AddTransactionUiState> = _uiState

    fun saveTransaction(transaction: Transaction, isEdit: Boolean) {
        _uiState.value = AddTransactionUiState.Loading

        viewModelScope.launch {
            val result = if (isEdit) {
                updateTransactionUseCase(transaction)
            } else {
                saveTransactionUseCase(transaction)
            }

            result.onSuccess {
                _uiState.value = AddTransactionUiState.Success
            }.onFailure { e ->
                _uiState.value = AddTransactionUiState.Error(
                    e.localizedMessage ?: "Error al guardar la transacción."
                )
            }
        }
    }

    fun resetState() {
        _uiState.value = AddTransactionUiState.Idle
    }
}
