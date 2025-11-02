package com.tuempresa.aprendeapp.ui.auth

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.tuempresa.aprendeapp.R
import com.tuempresa.aprendeapp.databinding.ActivityRegisterBinding
import com.tuempresa.aprendeapp.utils.showToast
import com.tuempresa.aprendeapp.viewmodel.AuthViewModel

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private val viewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUI()
        observeViewModel()
    }

    private fun setupUI() {
        binding.apply {
            btnRegister.setOnClickListener {
                val name = etName.text.toString().trim()
                val email = etEmail.text.toString().trim()
                val password = etPassword.text.toString()
                val confirmPassword = etConfirmPassword.text.toString()

                viewModel.register(name, email, password, confirmPassword)
            }

            tvLoginLink.setOnClickListener {
                finish()
            }
        }
    }

    private fun observeViewModel() {
        viewModel.authState.observe(this) { state ->
            when (state) {
                is AuthViewModel.AuthState.RegisterSuccess -> {
                    showToast(getString(R.string.register_success))
                    finish()
                }
                is AuthViewModel.AuthState.ValidationError -> {}
                is AuthViewModel.AuthState.Error -> {
                    showToast(state.message)
                }
                else -> {}
            }
        }

        viewModel.validationErrors.observe(this) { errors ->
            if (errors.isNotEmpty()) {
                showValidationErrorsDialog(errors)
            }
        }
    }

    private fun showValidationErrorsDialog(errors: List<String>) {
        val message = errors.joinToString("\n• ", prefix = "• ")
        AlertDialog.Builder(this)
            .setTitle("Error de validación")
            .setMessage("La contraseña debe cumplir:\n\n$message")
            .setPositiveButton("Entendido", null)
            .show()
    }
}