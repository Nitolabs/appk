package com.tuempresa.aprendeapp.ui.auth

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.tuempresa.aprendeapp.R
import com.tuempresa.aprendeapp.databinding.ActivityLoginBinding
import com.tuempresa.aprendeapp.ui.main.MainActivity
import com.tuempresa.aprendeapp.utils.showToast
import com.tuempresa.aprendeapp.viewmodel.AuthViewModel

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private val viewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (viewModel.isLoggedIn()) {
            navigateToMain()
            return
        }

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUI()
        observeViewModel()
    }

    private fun setupUI() {
        binding.apply {
            btnLogin.setOnClickListener {
                val email = etEmail.text.toString().trim()
                val password = etPassword.text.toString()
                viewModel.login(email, password)
            }

            tvRegisterLink.setOnClickListener {
                startActivity(Intent(this@LoginActivity, RegisterActivity::class.java))
            }
        }
    }

    private fun observeViewModel() {
        viewModel.authState.observe(this) { state ->
            when (state) {
                is AuthViewModel.AuthState.Success -> {
                    showToast(getString(R.string.login_success))
                    navigateToMain()
                }
                is AuthViewModel.AuthState.Error -> {
                    showToast(state.message)
                }
                else -> {}
            }
        }
    }

    private fun navigateToMain() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}