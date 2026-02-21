package com.example.bankapp.ui.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bankapp.domain.model.Profession
import com.example.bankapp.domain.model.ValidationResult
import com.example.bankapp.domain.usecase.GetProfessionsUseCase
import com.example.bankapp.domain.usecase.SignUpUseCase
import com.example.bankapp.domain.usecase.ValidateRegistrationFieldsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegistrationViewModel @Inject constructor(
    private val signUpUseCase: SignUpUseCase,
    private val validationUseCase: ValidateRegistrationFieldsUseCase,
    private val getProfessionsUseCase: GetProfessionsUseCase
) : ViewModel() {

    private val _registrationState = MutableStateFlow<RegistrationState>(RegistrationState.Idle)
    val registrationState: StateFlow<RegistrationState> = _registrationState.asStateFlow()

    private val _professions = MutableStateFlow<List<Profession>>(emptyList())
    val professions: StateFlow<List<Profession>> = _professions.asStateFlow()

    private var loadProfessionsJob: Job? = null

    init {
        loadProfessions()
    }

    private fun loadProfessions() {
        loadProfessionsJob?.cancel()
        loadProfessionsJob = viewModelScope.launch {
            try {
                val list = getProfessionsUseCase()
                _professions.value = list
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun startRegistration(
        name: String,
        email: String,
        password: String,
        income: String,
        expenses: String,
        profession: String
    ) {
        val trimmedName = name.trim()
        val trimmedEmail = email.trim()
        val trimmedPassword = password.trim()
        val trimmedIncome = income.trim()
        val trimmedExpenses = expenses.trim()
        val trimmedProfession = profession.trim()

        _registrationState.value = RegistrationState.Idle

        val validationResultMap = validationUseCase(
            trimmedName,
            trimmedEmail,
            trimmedPassword,
            trimmedIncome,
            trimmedExpenses,
            trimmedProfession
        )

        val hasErrors = validationResultMap.any { (_, result) -> result is ValidationResult.Invalid }

        if (hasErrors) {
            _registrationState.value = RegistrationState.ValidationErrors(validationResultMap)
            return
        }

        _registrationState.value = RegistrationState.Loading

        viewModelScope.launch {
            try {
                val incomeValue = trimmedIncome.replace(',', '.').toDoubleOrNull() ?: 0.0
                val expensesValue = trimmedExpenses.replace(',', '.').toDoubleOrNull() ?: 0.0

                val result = signUpUseCase(
                    trimmedName, trimmedEmail, trimmedPassword, incomeValue, expensesValue, trimmedProfession
                )

                result.onSuccess { user ->
                    _registrationState.value = RegistrationState.Success(user.uid)
                }.onFailure { e ->
                    _registrationState.value =
                        RegistrationState.Error(e.localizedMessage ?: "Error desconocido en el registro.")
                }

            } catch (e: Exception) {
                _registrationState.value =
                    RegistrationState.Error(e.localizedMessage ?: "Error desconocido en el registro.")
            }
        }
    }

    fun resetState() {
        _registrationState.value = RegistrationState.Idle
    }
}