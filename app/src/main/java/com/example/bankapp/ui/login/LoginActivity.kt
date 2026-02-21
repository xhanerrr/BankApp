package com.example.bankapp.ui.login

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.bankapp.R
import com.example.bankapp.databinding.ActivityLoginBinding
import com.example.bankapp.domain.model.ValidationResult
import com.example.bankapp.ui.main.MainActivity
import com.example.bankapp.ui.register.RegisterActivity
import com.example.bankapp.ui.utils.GenericTextWatcher
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val viewModel: LoginViewModel by viewModels()

    private fun color(id: Int) = ContextCompat.getColor(this, id)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val defaultTextColor = binding.loginBtn.currentTextColor

        setupTextWatchers()
        setupListeners()
        observeViewModel(defaultTextColor)
    }

    private fun setupTextWatchers() {
        val resetUI = {
            binding.errorTextView.visibility = View.GONE
            binding.emailCardView.setBackgroundResource(R.drawable.cardview_normal)
            binding.passwordCardView.setBackgroundResource(R.drawable.cardview_normal)
        }

        binding.emailEditText.addTextChangedListener(
            GenericTextWatcher(
                onChange = { text -> viewModel.updateEmail(text) },
                onResetUI = resetUI
            )
        )

        binding.passwordEditText.addTextChangedListener(
            GenericTextWatcher(
                onChange = { text -> viewModel.updatePassword(text) },
                onResetUI = resetUI
            )
        )
    }

    private fun setupListeners() {
        binding.signUpIntent.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        binding.loginBtn.setOnClickListener {
            binding.errorTextView.visibility = View.GONE
            binding.emailInputLayout.isErrorEnabled = false
            binding.passwordInputLayout.isErrorEnabled = false

            val email = binding.emailEditText.text.toString().trim()
            val password = binding.passwordEditText.text.toString().trim()

            viewModel.startLogin(email, password)
        }
    }

    private fun observeViewModel(defaultTextColor: Int) {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.isLoginButtonEnabled.collect { isEnabled ->

                    val bgColor = color(
                        if (isEnabled) R.color.login_button_original
                        else R.color.login_button_disabled
                    )

                    val d = DrawableCompat.wrap(binding.loginBtn.background).mutate()
                    DrawableCompat.setTint(d, bgColor)
                    binding.loginBtn.background = d

                    val textColor = color(
                        if (isEnabled) R.color.login_button_text_enabled
                        else R.color.login_button_text_disabled
                    )

                    binding.loginBtn.setTextColor(textColor)
                    binding.loginBtn.isEnabled = isEnabled
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.loginState.collect { state ->
                    handleLoginState(state)
                }
            }
        }
    }

    private fun handleLoginState(state: LoginState) {
        when (state) {
            is LoginState.Idle -> {
                clearValidationErrors()
                binding.errorTextView.visibility = View.GONE
            }
            is LoginState.Loading -> {
                binding.progressBarLogin.visibility = View.VISIBLE
                setAllVisibility(false)
                clearValidationErrors()
                binding.errorTextView.visibility = View.GONE
            }
            is LoginState.Success -> {
                val intent = Intent(this, MainActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
                startActivity(intent)
                Toast.makeText(this, "Login Exitoso! Usuario: ${state.userId}", Toast.LENGTH_SHORT).show()
                viewModel.resetState()
            }
            is LoginState.ValidationErrors -> {
                displayValidationErrors(state.errors)
            }
            is LoginState.Error -> {
                binding.progressBarLogin.visibility = View.GONE
                setAllVisibility(true)
                binding.errorTextView.text = "Email o contraseña incorrectos."
                binding.errorTextView.visibility = View.VISIBLE
                binding.emailCardView.setBackgroundResource(R.drawable.cardview_error_border)
                binding.passwordCardView.setBackgroundResource(R.drawable.cardview_error_border)
            }
        }
    }

    private fun displayValidationErrors(errors: Map<String, ValidationResult>) {
        clearValidationErrors()

        errors.forEach { (fieldKey, result) ->
            if (result is ValidationResult.Invalid) {
                val msg = result.errorMessage
                when (fieldKey) {
                    "email" -> binding.emailInputLayout.error = msg
                    "password" -> binding.passwordInputLayout.error = msg
                }
            }
        }
    }

    private fun clearValidationErrors() {
        binding.emailInputLayout.error = null
        binding.passwordInputLayout.error = null
        binding.emailInputLayout.isErrorEnabled = false
        binding.passwordInputLayout.isErrorEnabled = false
    }

    private fun setAllVisibility(isVisible: Boolean) {
        val v = if (isVisible) View.VISIBLE else View.GONE
        binding.imageView.visibility = v
        binding.emailCardView.visibility = v
        binding.passwordCardView.visibility = v
        binding.loginBtn.visibility = v
        binding.signUpIntent.visibility = v
        binding.forgotPassword.visibility = v
    }
}
    