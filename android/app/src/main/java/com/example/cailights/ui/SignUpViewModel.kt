package com.example.cailights.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cailights.domain.auth.AuthRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SignUpState(
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val isPasswordVisible: Boolean = false,
    val isConfirmPasswordVisible: Boolean = false,
    val verificationCode: String = "",
    val signUpStep: SignUpStep = SignUpStep.FORM,
    val isLoading: Boolean = false,
    val emailError: UiText? = null,
    val passwordError: UiText? = null,
    val confirmPasswordError: UiText? = null
)

enum class SignUpStep {
    FORM,
    VERIFICATION
}

sealed interface SignUpAction {
    data class OnEmailChange(val email: String) : SignUpAction
    data class OnPasswordChange(val password: String) : SignUpAction
    data class OnConfirmPasswordChange(val confirmPassword: String) : SignUpAction
    data object OnTogglePasswordVisibility : SignUpAction
    data object OnToggleConfirmPasswordVisibility : SignUpAction
    data class OnVerificationCodeChange(val code: String) : SignUpAction
    data object OnCreateAccountClick : SignUpAction
    data object OnVerifyClick : SignUpAction
    data object OnBackClick : SignUpAction
    data object OnSignInClick : SignUpAction
}

sealed interface SignUpEvent {
    data object NavigateToSignIn : SignUpEvent
    data object SignUpSuccess : SignUpEvent
    data class ShowError(val message: UiText) : SignUpEvent
}

class SignUpViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _state = MutableStateFlow(SignUpState())
    val state = _state.asStateFlow()

    private val _events = Channel<SignUpEvent>()
    val events = _events.receiveAsFlow()

    fun onAction(action: SignUpAction) {
        when (action) {
            is SignUpAction.OnEmailChange -> {
                _state.update { it.copy(email = action.email, emailError = null) }
            }
            is SignUpAction.OnPasswordChange -> {
                _state.update { it.copy(password = action.password, passwordError = null) }
            }
            is SignUpAction.OnConfirmPasswordChange -> {
                _state.update { it.copy(confirmPassword = action.confirmPassword, confirmPasswordError = null) }
            }
            SignUpAction.OnTogglePasswordVisibility -> {
                _state.update { it.copy(isPasswordVisible = !it.isPasswordVisible) }
            }
            SignUpAction.OnToggleConfirmPasswordVisibility -> {
                _state.update { it.copy(isConfirmPasswordVisible = !it.isConfirmPasswordVisible) }
            }
            is SignUpAction.OnVerificationCodeChange -> {
                _state.update { it.copy(verificationCode = action.code) }
            }
            SignUpAction.OnCreateAccountClick -> {
                validateAndProceed()
            }
            SignUpAction.OnVerifyClick -> {
                verifyCode()
            }
            SignUpAction.OnBackClick -> {
                _state.update { it.copy(signUpStep = SignUpStep.FORM) }
            }
            SignUpAction.OnSignInClick -> {
                _events.trySend(SignUpEvent.NavigateToSignIn)
            }
        }
    }

    private fun signUp() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            authRepository.signUp(_state.value.email, _state.value.password)
                .onSuccess {
                    _state.update { it.copy(isLoading = false, signUpStep = SignUpStep.VERIFICATION) }
                }
                .onFailure { error ->
                    _state.update { it.copy(isLoading = false) }
                    val message = when(error) {
                        AuthError.UserAlreadyExists -> UiText.DynamicString("User already exists")
                        else -> UiText.DynamicString("An unknown error occurred")
                    }
                    _events.send(SignUpEvent.ShowError(message))
                }
        }
    }

    private fun verifyCode() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            authRepository.verifyCode(_state.value.email, _state.value.verificationCode)
                .onSuccess {
                    _state.update { it.copy(isLoading = false) }
                    _events.send(SignUpEvent.SignUpSuccess)
                }
                .onFailure {
                    _state.update { it.copy(isLoading = false) }
                    _events.send(SignUpEvent.ShowError(UiText.DynamicString("Invalid verification code")))
                }
        }
    }

    private fun validateAndProceed() {
        val currentState = _state.value
        var hasError = false
        
        var emailError: UiText? = null
        var passwordError: UiText? = null
        var confirmPasswordError: UiText? = null

        if (currentState.email.isBlank()) {
            emailError = UiText.DynamicString("Email is required")
            hasError = true
        }
        if (currentState.password.isBlank()) {
            passwordError = UiText.DynamicString("Password is required")
            hasError = true
        }
        if (currentState.confirmPassword != currentState.password) {
            confirmPasswordError = UiText.DynamicString("Passwords do not match")
            hasError = true
        } else if (currentState.confirmPassword.isBlank()) {
            confirmPasswordError = UiText.DynamicString("Please confirm your password")
            hasError = true
        }

        if (hasError) {
            _state.update { 
                it.copy(
                    emailError = emailError,
                    passwordError = passwordError,
                    confirmPasswordError = confirmPasswordError
                )
            }
        } else {
            signUp()
        }
    }
}
