package com.example.login.viewmodels

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.login.repository.AuthRepository
import com.google.firebase.Timestamp
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    var uiState by mutableStateOf(LoginUiState())
        private set

    private val _loginState = mutableStateOf<LoginState>(LoginState.Idle)
    val loginState: MutableState<LoginState> = _loginState

    fun onEmailChange(email: String) {
        uiState = uiState.copy(email = email)
    }

    fun onPasswordChange(password: String) {
        uiState = uiState.copy(password = password)
    }

    fun login(onSuccess: () -> Unit) {
        viewModelScope.launch {
            _loginState.value = LoginState.Loading
            uiState = uiState.copy(isLoading = true)
            val result = authRepository.login(uiState.email, uiState.password)
            uiState = if (result.isSuccess) {
                onSuccess()
                LoginUiState()
            } else {
                uiState.copy(errorMessage = result.exceptionOrNull()?.message, isLoading = false)
            }
        }
    }

    fun handleGoogleSignIn(userId: String, nome: String, email: String, dataNascimento: String) {
        viewModelScope.launch {
            _loginState.value = LoginState.Loading
            uiState = uiState.copy(isLoading = true)
            val userExists = authRepository.checkIfUserExists(userId)
            if (!userExists) {
                val dateBirthdayTimestamp = try {
                    val formatter = SimpleDateFormat("dd/MM/yyyy", Locale("pt", "BR"))
                    val dateBirthday = formatter.parse(dataNascimento)
                    dateBirthday?.let { Timestamp(it) }
                    } catch (ex: Exception) {
                        Timestamp.now()
                    }
                authRepository.createUser(nome, email, dateBirthdayTimestamp)
            }
            _loginState.value = LoginState.Success
        }
    }

    fun loginWithGoogle(idToken: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _loginState.value = LoginState.Loading
            uiState = uiState.copy(isLoading = true)
            val result = authRepository.loginWithGoogle(idToken)
            uiState = if (result.isSuccess) {
                onSuccess()
                LoginUiState()
            } else {
                uiState.copy(errorMessage = result.exceptionOrNull()?.message, isLoading = false)
            }
        }
    }
}

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

sealed class LoginState {
    object Idle : LoginState()
    object Loading : LoginState()
    object Success : LoginState()
    data class Error(val message: String) : LoginState()
}
