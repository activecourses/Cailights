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

data class SignInState(
    val email: String = "",
    val password: String = "",
    val signInStep: SignInStep = SignInStep.EMAIL,
    val isLoading: Boolean = false
)

enum class SignInStep {
    EMAIL,
    PASSWORD,
    FORGOT_PASSWORD
}

sealed interface SignInAction {
    data class OnEmailChange(val email: String) : SignInAction
    data class OnPasswordChange(val password: String) : SignInAction
    data object OnNextClick : SignInAction
    data object OnForgotPasswordClick : SignInAction
    data object OnBackClick : SignInAction
    data object OnSignInClick : SignInAction
    data object OnSendVerificationCodeClick : SignInAction
    data object OnSignUpClick : SignInAction
}

sealed interface SignInEvent {
    data object NavigateToSignUp : SignInEvent
    data object SignInSuccess : SignInEvent
    data class ShowError(val message: UiText) : SignInEvent
}

class SignInViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _state = MutableStateFlow(SignInState())
    val state = _state.asStateFlow()

    private val _events = Channel<SignInEvent>()
    val events = _events.receiveAsFlow()

    fun onAction(action: SignInAction) {
        when (action) {
            is SignInAction.OnEmailChange -> {
                _state.update { it.copy(email = action.email) }
            }
            is SignInAction.OnPasswordChange -> {
                _state.update { it.copy(password = action.password) }
            }
            SignInAction.OnNextClick -> {
                _state.update { it.copy(signInStep = SignInStep.PASSWORD) }
            }
            SignInAction.OnForgotPasswordClick -> {
                _state.update { it.copy(signInStep = SignInStep.FORGOT_PASSWORD) }
            }
            SignInAction.OnBackClick -> {
                _state.update {
                    val nextStep = if (it.signInStep == SignInStep.FORGOT_PASSWORD) {
                        SignInStep.EMAIL
                    } else {
                        SignInStep.EMAIL
                    }
                    it.copy(signInStep = nextStep)
                }
            }
            SignInAction.OnSignInClick -> {
                signIn()
            }
            SignInAction.OnSendVerificationCodeClick -> {
                sendVerificationCode()
            }
            SignInAction.OnSignUpClick -> {
                _events.trySend(SignInEvent.NavigateToSignUp)
            }
        }
    }

    private fun signIn() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            authRepository.signIn(_state.value.email, _state.value.password)
                .onSuccess {
                    _state.update { it.copy(isLoading = false) }
                    _events.send(SignInEvent.SignInSuccess)
                }
                .onFailure { error ->
                    _state.update { it.copy(isLoading = false) }
                    val message = when(error) {
                        AuthError.InvalidCredentials -> UiText.DynamicString("Invalid email or password")
                        else -> UiText.DynamicString("An unknown error occurred")
                    }
                    _events.send(SignInEvent.ShowError(message))
                }
        }
    }

    private fun sendVerificationCode() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            authRepository.sendVerificationCode(_state.value.email)
                .onSuccess {
                    _state.update { it.copy(isLoading = false) }
                    _events.send(SignInEvent.ShowError(UiText.DynamicString("Verification code sent!")))
                }
                .onFailure {
                    _state.update { it.copy(isLoading = false) }
                    _events.send(SignInEvent.ShowError(UiText.DynamicString("Failed to send code")))
                }
        }
    }
}
