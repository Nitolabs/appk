package com.tuempresa.aprendeapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.tuempresa.aprendeapp.data.model.User
import com.tuempresa.aprendeapp.data.repository.AuthRepository
import com.tuempresa.aprendeapp.utils.PasswordValidator

class AuthViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = AuthRepository(application)

    private val _authState = MutableLiveData<AuthState>()
    val authState: LiveData<AuthState> = _authState

    private val _validationErrors = MutableLiveData<List<String>>()
    val validationErrors: LiveData<List<String>> = _validationErrors

    fun login(email: String, password: String) {
        if (email.isEmpty() || password.isEmpty()) {
            _authState.value = AuthState.Error("Complete todos los campos")
            return
        }

        val success = repository.login(email, password)
        _authState.value = if (success) {
            AuthState.Success
        } else {
            AuthState.Error("Credenciales inválidas")
        }
    }

    fun register(name: String, email: String, password: String, confirmPassword: String) {
        // Validar campos vacíos
        if (name.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            _authState.value = AuthState.Error("Complete todos los campos")
            return
        }

        // Validar email
        if (!email.contains("@")) {
            _authState.value = AuthState.Error("Correo electrónico inválido")
            return
        }

        // Validar contraseña
        val passwordValidation = PasswordValidator.validate(password)
        if (!passwordValidation.isValid) {
            _validationErrors.value = passwordValidation.errors
            _authState.value = AuthState.ValidationError
            return
        }

        // Validar coincidencia de contraseñas
        if (!PasswordValidator.validateMatch(password, confirmPassword)) {
            _authState.value = AuthState.Error("Las contraseñas no coinciden")
            return
        }

        // Registrar usuario
        val user = User(
            name = name,
            email = email,
            password = password
        )

        val success = repository.register(user)
        _authState.value = if (success) {
            AuthState.RegisterSuccess
        } else {
            AuthState.Error("El correo ya está registrado")
        }
    }

    fun logout() {
        repository.logout()
        _authState.value = AuthState.LoggedOut
    }

    fun isLoggedIn(): Boolean {
        return repository.isLoggedIn()
    }

    fun getCurrentUser(): User? {
        return repository.getCurrentUser()
    }

    sealed class AuthState {
        object Success : AuthState()
        object RegisterSuccess : AuthState()
        object LoggedOut : AuthState()
        object ValidationError : AuthState()
        data class Error(val message: String) : AuthState()
    }
}