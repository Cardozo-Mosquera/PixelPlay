package com.equipo.pixelplay.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.equipo.pixelplay.data.repository.AuthRepository
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * Mapea excepciones de Firebase Auth a mensajes en español.
 * `internal` (no private) para poder testearla como función pura desde el módulo.
 */
internal fun mapAuthErrorToSpanish(e: Throwable): String = when (e) {
    is FirebaseAuthWeakPasswordException -> "La contraseña debe tener al menos 6 caracteres"
    is FirebaseAuthInvalidCredentialsException -> "Correo o contraseña incorrectos"
    is FirebaseAuthInvalidUserException -> "Correo o contraseña incorrectos"
    is FirebaseAuthUserCollisionException -> "Ese correo ya está registrado"
    is FirebaseNetworkException -> "Sin conexión. Revisa tu internet"
    else -> "Ocurrió un error. Inténtalo de nuevo"
}

class AuthViewModel(
    private val repo: AuthRepository = AuthRepository()
) : ViewModel() {

    /** true = hay usuario. Semilla con el usuario actual para no parpadear el login. */
    val authState: StateFlow<Boolean> = repo.authState.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = repo.currentUser != null
    )

    private val _formState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val formState: StateFlow<AuthUiState> = _formState.asStateFlow()

    fun login(email: String, pass: String) {
        if (!validate(email, pass)) return
        _formState.value = AuthUiState.Loading
        viewModelScope.launch {
            try {
                repo.login(email.trim(), pass)
                // En éxito no navegamos: el gate reacciona a authState.
                _formState.value = AuthUiState.Idle
            } catch (e: Exception) {
                _formState.value = AuthUiState.Error(mapAuthErrorToSpanish(e))
            }
        }
    }

    fun register(email: String, pass: String) {
        if (!validate(email, pass)) return
        _formState.value = AuthUiState.Loading
        viewModelScope.launch {
            try {
                repo.register(email.trim(), pass)
                _formState.value = AuthUiState.Idle
            } catch (e: Exception) {
                _formState.value = AuthUiState.Error(mapAuthErrorToSpanish(e))
            }
        }
    }

    fun logout() = repo.logout()

    fun clearError() {
        _formState.value = AuthUiState.Idle
    }

    /** Valida campos no vacíos y contraseña >= 6; publica Error si no cumple. */
    private fun validate(email: String, pass: String): Boolean {
        if (email.isBlank() || pass.isBlank()) {
            _formState.value = AuthUiState.Error("Completa correo y contraseña")
            return false
        }
        if (pass.length < 6) {
            _formState.value = AuthUiState.Error("La contraseña debe tener al menos 6 caracteres")
            return false
        }
        return true
    }
}
