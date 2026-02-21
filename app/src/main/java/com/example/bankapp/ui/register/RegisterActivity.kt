package com.example.bankapp.ui.register

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.bankapp.R
import com.example.bankapp.databinding.ActivityRegisterBinding
import com.example.bankapp.domain.model.ValidationResult
import com.example.bankapp.ui.login.LoginActivity
import com.example.bankapp.ui.main.MainActivity
import com.example.bankapp.ui.register.bottomsheet.ProfessionSelectionBottomSheet
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.Locale

@AndroidEntryPoint
class RegisterActivity : AppCompatActivity(), ProfessionSelectionBottomSheet.ProfessionSelectionListener {

    private lateinit var binding: ActivityRegisterBinding
    private val viewModel: RegistrationViewModel by viewModels()
    private var backPressedEnabled = true
    private lateinit var backCallback: OnBackPressedCallback

    private data class FieldGroup(
        val key: String,
        val input: TextView,
        val card: CardView
    )

    private val fields: List<FieldGroup> by lazy {
        listOf(
            FieldGroup("name", binding.nameEditText, binding.nameCardView),
            FieldGroup("email", binding.emailEditText, binding.emailCardView),
            FieldGroup("password", binding.passwordEditText, binding.passwordCardView),
            FieldGroup("income", binding.incomeEditText, binding.financialsCardView),
            FieldGroup("expenses", binding.expensesEditText, binding.financialsCardView),
            FieldGroup("profession", binding.professionAutoCompleteTextView, binding.professionCardView)
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        backCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (backPressedEnabled) {
                    isEnabled = false
                    onBackPressedDispatcher.onBackPressed()
                }
            }
        }
        onBackPressedDispatcher.addCallback(this, backCallback)

        setupListeners()
        setupTextWatchers()
        observeStateFlows()
    }

    private fun setupListeners() {
        binding.signUpButton.setOnClickListener {
            viewModel.startRegistration(
                binding.nameEditText.text.toString().trim(),
                binding.emailEditText.text.toString().trim(),
                binding.passwordEditText.text.toString().trim(),
                binding.incomeEditText.text.toString().trim(),
                binding.expensesEditText.text.toString().trim(),
                binding.professionAutoCompleteTextView.text.toString().trim()
            )
        }

        binding.loginIntentTextView.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        binding.professionAutoCompleteTextView.setOnClickListener {
            openProfessionSelectionBottomSheet()
        }
    }

    private fun setupTextWatchers() {
        fields.forEach { fg ->
            fg.input.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    fg.input.error = null
                    fg.card.setBackgroundResource(R.drawable.cardview_normal)
                }
                override fun afterTextChanged(s: Editable?) {}
            })
        }
    }

    private fun observeStateFlows() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.registrationState.collect { state -> handleRegistrationState(state) }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.professions.collect { }
            }
        }
    }

    private fun handleRegistrationState(state: RegistrationState) {
        val isEnabled = state !is RegistrationState.Loading
        binding.signUpButton.isEnabled = isEnabled

        when (state) {
            is RegistrationState.Idle -> {
                backPressedEnabled = true
                clearAllErrors()
            }

            is RegistrationState.Loading -> {
                backPressedEnabled = false
                binding.progressBar.visibility = View.VISIBLE
                setAllVisibility(false)
            }

            is RegistrationState.Success -> {
                backPressedEnabled = true
                Toast.makeText(this, "¡Registro exitoso! ID: ${state.userId}", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, MainActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                })
                viewModel.resetState()
            }

            is RegistrationState.Error -> {
                backPressedEnabled = true
                binding.progressBar.visibility = View.GONE
                setAllVisibility(true)

                val errorMsg = state.errorType.lowercase(Locale.ROOT)

                when {
                    errorMsg.contains("correo") -> setFieldError("email", state.errorType)
                    errorMsg.contains("contraseña") -> setFieldError("password", state.errorType)
                    else -> Toast.makeText(this, "Error: ${state.errorType}", Toast.LENGTH_LONG).show()
                }
                viewModel.resetState()
            }

            is RegistrationState.ValidationErrors -> {
                backPressedEnabled = true
                binding.progressBar.visibility = View.GONE
                setAllVisibility(true)
                displayValidationErrors(state.errors)
            }
        }
    }

    private fun openProfessionSelectionBottomSheet() {
        val professions = viewModel.professions.value.map { it.name }
        if (professions.isNotEmpty()) {
            ProfessionSelectionBottomSheet
                .newInstance(ArrayList(professions))
                .show(supportFragmentManager, "ProfessionSelectionBottomSheet")
        } else {
            Toast.makeText(this, "Cargando profesiones...", Toast.LENGTH_SHORT).show()
        }
    }

    private fun displayValidationErrors(errors: Map<String, ValidationResult>) {
        clearAllErrors()
        var firstError: TextView? = null

        errors.forEach { (key, result) ->
            if (result is ValidationResult.Invalid) {
                setFieldError(key, result.errorMessage)?.let { input ->
                    if (firstError == null) firstError = input
                }
            }
        }

        firstError?.requestFocus()
    }

    private fun setFieldError(key: String, message: String): TextView? {
        val fg = fields.find { it.key == key } ?: return null

        val icon = ContextCompat.getDrawable(this, R.drawable.close)?.apply {
            setBounds(0, 0, intrinsicWidth, intrinsicHeight)
        }

        fg.input.setError(message, icon)
        fg.card.setBackgroundResource(R.drawable.cardview_error_border)

        return fg.input
    }

    private fun clearAllErrors() {
        fields.forEach { fg ->
            fg.input.error = null
            fg.card.setBackgroundResource(R.drawable.cardview_normal)
        }
    }

    private fun setAllVisibility(isVisible: Boolean) {
        val v = if (isVisible) View.VISIBLE else View.GONE
        binding.logoImageView.visibility = v
        binding.nameCardView.visibility = v
        binding.passwordCardView.visibility = v
        binding.emailCardView.visibility = v
        binding.financialsCardView.visibility = v
        binding.professionCardView.visibility = v
        binding.signUpButton.visibility = v
        binding.loginIntentTextView.visibility = v
    }

    override fun onProfessionSelected(profession: String) {
        binding.professionAutoCompleteTextView.setText(profession)
        binding.professionAutoCompleteTextView.error = null
        binding.professionCardView.setBackgroundResource(R.drawable.cardview_normal)
    }
}
