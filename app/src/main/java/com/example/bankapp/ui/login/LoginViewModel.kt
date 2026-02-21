package com.example.bankapp.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bankapp.domain.model.ValidationResult
import com.example.bankapp.domain.usecase.LoginUseCase
import com.example.bankapp.domain.usecase.ValidateLoginFieldsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class LoginState {
    object Idle : LoginState()
    object Loading : LoginState()
    data class Success(val userId: String) : LoginState()
    data class ValidationErrors(val errors: Map<String, ValidationResult>) : LoginState()
    data class Error(val message: String) : LoginState()
}

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val validateLoginFieldsUseCase: ValidateLoginFieldsUseCase
) : ViewModel() {

    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState: StateFlow<LoginState> = _loginState.asStateFlow()

    private val emailInput = MutableStateFlow("")
    private val passwordInput = MutableStateFlow("")

    val isLoginButtonEnabled: StateFlow<Boolean> = combine(
        emailInput,
        passwordInput
    ) { email, password ->
        email.trim().isNotEmpty() && password.trim().isNotEmpty()
    }.stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        false
    )

    fun updateEmail(email: String) {
        emailInput.value = email
    }

    fun updatePassword(password: String) {
        passwordInput.value = password
    }

    fun startLogin(email: String, password: String) {
        _loginState.value = LoginState.Idle

        val errors = validateLoginFieldsUseCase(email, password)

        if (errors.isNotEmpty()) {
            _loginState.value = LoginState.ValidationErrors(errors)
            return
        }

        if (!isPasswordValid(password)) {
            _loginState.value = LoginState.Error("Email o contraseña incorrectos.")
            return
        }

        _loginState.value = LoginState.Loading

        viewModelScope.launch {
            val result = loginUseCase(email.trim(), password.trim())

            result.onSuccess { user ->
                _loginState.value = LoginState.Success(user.uid)

            }.onFailure {
                _loginState.value = LoginState.Error("Email o contraseña incorrectos.")
            }
        }
    }
    private fun isPasswordValid(password: String): Boolean {
        val hasUpper = password.any { it.isUpperCase() }
        val hasDigit = password.any { it.isDigit() }
        return password.length >= 6 && hasUpper && hasDigit
    }


    fun resetState() {
        _loginState.value = LoginState.Idle
    }
}