package com.portfolio.auth.domain

class RegisterCredentialValidator(
    private val emailValidator: EmailValidator
) {

    fun isValidEmail(email: String): Boolean {
        return emailValidator.validate(email)
    }

    fun validatePassword(password: String): PasswordValidationState {

        val hasMinLength = password.length >= PASSWORD_MIN_LENGTH
        val hasNumber = password.contains(Regex("[0-9]"))
        val hasLowerCaseCharacter = password.contains(Regex("[a-z]"))
        val hasUpperCaseCharacter = password.contains(Regex("[A-Z]"))

        return PasswordValidationState(
            hasMinLength = hasMinLength,
            hasNumber = hasNumber,
            hasLowerCaseCharacter = hasLowerCaseCharacter,
            hasUpperCaseCharacter = hasUpperCaseCharacter
        )
    }

    companion object {
        const val PASSWORD_MIN_LENGTH = 6
    }

}