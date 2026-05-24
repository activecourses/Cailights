package com.example.cailights.ui

/**
 * A simple Result wrapper following the project's error handling patterns.
 */
sealed interface Result<out D, out E: Error> {
    data class Success<out D>(val data: D): Result<D, Nothing>
    data class Error<out E: com.example.cailights.ui.Error>(val error: E): Result<Nothing, E>
}

sealed interface Error

sealed interface AuthError: Error {
    data object InvalidCredentials : AuthError
    data object UserAlreadyExists : AuthError
    data object NetworkError : AuthError
    data object UnknownError : AuthError
}

typealias EmptyResult<E> = Result<Unit, E>

inline fun <D, E: Error, R> Result<D, E>.map(transform: (D) -> R): Result<R, E> {
    return when(this) {
        is Result.Error -> Result.Error(error)
        is Result.Success -> Result.Success(transform(data))
    }
}

fun <D, E: Error> Result<D, E>.asEmptyResult(): EmptyResult<E> {
    return map {  }
}

inline fun <D, E: Error> Result<D, E>.onSuccess(action: (D) -> Unit): Result<D, E> {
    if(this is Result.Success) action(data)
    return this
}

inline fun <D, E: Error> Result<D, E>.onFailure(action: (E) -> Unit): Result<D, E> {
    if(this is Result.Error) action(error)
    return this
}
