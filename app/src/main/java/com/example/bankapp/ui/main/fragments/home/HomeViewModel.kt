package com.example.bankapp.ui.main.fragments.home

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bankapp.domain.model.HomeUiState
import com.example.bankapp.domain.model.UploadState
import com.example.bankapp.domain.repository.AuthRepository
import com.example.bankapp.domain.repository.TransactionRepository
import com.example.bankapp.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val transactionRepository: TransactionRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState

    private val _uploadState = MutableStateFlow<UploadState>(UploadState.Idle)
    val uploadState: StateFlow<UploadState> = _uploadState

    private var profileJob: Job? = null
    private var transactionsJob: Job? = null

    init {
        loadUserData()
    }

    fun refresh() {
        loadUserData()
    }

    private fun loadUserData() {
        val uid = authRepository.getCurrentUserUid() ?: return

        profileJob?.cancel()
        transactionsJob?.cancel()

        profileJob = observeUserProfile(uid)
        transactionsJob = observeUserTransactions(uid)
    }

    private fun observeUserProfile(uid: String): Job {
        return userRepository.getUserProfile(uid)
            .onEach { result ->
                result.onSuccess { user ->
                    val oldTransactions = (_uiState.value as? HomeUiState.Success)?.recentTransactions ?: emptyList()
                    _uiState.value = HomeUiState.Success(user, oldTransactions)
                }.onFailure {
                    _uiState.value = HomeUiState.Error(it.localizedMessage ?: "Error al cargar usuario.")
                }
            }
            .launchIn(viewModelScope)
    }

    private fun observeUserTransactions(uid: String): Job {
        return transactionRepository.getTransactions(uid)
            .onEach { result ->
                val current = _uiState.value as? HomeUiState.Success ?: return@onEach

                result.onSuccess { transactions ->
                    val income = transactions.filter {
                        it.type.equals("Income", true) || it.type.equals("Receive", true)
                    }.sumOf { it.amount }

                    val expenses = transactions.filter {
                        it.type.equals("Expense", true) || it.type.equals("Sent", true)
                    }.sumOf { it.amount }

                    val updatedUser = current.user.copy(
                        income = current.user.income + income,
                        expenses = current.user.expenses + expenses
                    )

                    _uiState.value = HomeUiState.Success(updatedUser, transactions)
                }.onFailure {
                    _uiState.value = HomeUiState.Error(it.localizedMessage ?: "Error al cargar transacciones.")
                }
            }
            .launchIn(viewModelScope)
    }

    fun uploadProfileImage(uri: Uri) {
        val uid = authRepository.getCurrentUserUid() ?: return

        _uploadState.value = UploadState.Loading

        viewModelScope.launch {
            val result = userRepository.uploadProfilePicture(uid, uri)

            result.onSuccess {
                _uploadState.value = UploadState.Success
            }.onFailure {
                _uploadState.value = UploadState.Error(it.localizedMessage ?: "Error al subir la imagen.")
            }
        }
    }

    fun logout() {
        profileJob?.cancel()
        transactionsJob?.cancel()
        authRepository.logout()
        _uiState.value = HomeUiState.Loading
    }
}
