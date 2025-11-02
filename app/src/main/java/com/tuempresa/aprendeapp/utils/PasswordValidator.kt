package com.tuempresa.aprendeapp.utils

object PasswordValidator {
    data class ValidationResult(
        val isValid: Boolean,
        val errors: List<String> = emptyList()
    )

    fun validate(password: String): ValidationResult {
        val errors = mutableListOf<String>()

        if (password.length < 8) {
            errors.add("La contraseña debe tener al menos 8 caracteres")
        }
        if (!password.any { it.isUpperCase() }) {
            errors.add("Debe contener al menos una letra mayúscula")
        }
        if (!password.any { it.isLowerCase() }) {
            errors.add("Debe contener al menos una letra minúscula")
        }
        if (!password.any { it.isDigit() }) {
            errors.add("Debe contener al menos un número")
        }
        val specialChars = "!@#\$%^&*"
        if (!password.any { it in specialChars }) {
            errors.add("Debe contener al menos un carácter especial (!@#\$%^&*)")
        }

        return ValidationResult(isValid = errors.isEmpty(), errors = errors)
    }

    fun validateMatch(password: String, confirmPassword: String): Boolean {
        return password == confirmPassword
    }
}