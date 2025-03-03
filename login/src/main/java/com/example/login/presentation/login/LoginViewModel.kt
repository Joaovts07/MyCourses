package com.example.login.presentation.login

import android.app.Activity
import android.util.Log
import androidx.activity.result.ActivityResult
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.login.domain.usecase.CheckIfUserExistsUseCase
import com.example.login.domain.usecase.CreateUserUseCase
import com.example.login.domain.usecase.LoginUseCase
import com.example.login.domain.usecase.LoginWithGoogleUseCase
import com.example.login.validators.isValidEmail
import com.example.login.validators.isValidPassword
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.Timestamp
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val loginWithGoogleUseCase: LoginWithGoogleUseCase,
    private val createUserUseCase: CreateUserUseCase,
    private val checkIfUserExistsUseCase: CheckIfUserExistsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState: StateFlow<LoginState> = _loginState.asStateFlow()

    fun onEmailChange(email: String) {
        _uiState.update { it.copy(email = email) }
    }

    fun onPasswordChange(password: String) {
        _uiState.update { it.copy(password = password) }
    }

    fun login() {
        if (verifyFields(_uiState.value.email, _uiState.value.password)) {
            _loginState.value = LoginState.Error("Campos inválidos")
            return
        }
        viewModelScope.launch {
            _loginState.value = LoginState.Loading
            _uiState.update { it.copy(isLoading = true) }
            val result = loginUseCase(_uiState.value.email, _uiState.value.password)
            if (result.isSuccess) {
                _loginState.value = LoginState.Logged
                _uiState.update { LoginUiState() }
            } else {
                _uiState.update { it.copy(errorMessage = result.exceptionOrNull()?.message, isLoading = false) }
                _loginState.value =
                    LoginState.Error(result.exceptionOrNull()?.message ?: "Erro desconhecido")
            }
        }
    }

    private fun handleGoogleSignIn(nome: String, email: String, dataNascimento: String? = "") {
        viewModelScope.launch {
            _loginState.value = LoginState.Loading
            _uiState.update { it.copy(isLoading = true) }
            val userExists = checkIfUserExistsUseCase()
            if (!userExists) {
                val dateBirthdayTimestamp = try {
                    val formatter = SimpleDateFormat("dd/MM/yyyy", Locale("pt", "BR"))
                    val dateBirthday = dataNascimento?.let { formatter.parse(it) }
                    dateBirthday?.let { Timestamp(it) }
                } catch (ex: Exception) {
                    Timestamp.now()
                }
                createUserUseCase(nome, email, dateBirthdayTimestamp)
            }
            _loginState.value = LoginState.Logged
            _uiState.update { it.copy(isLoading = false) }
        }
    }

    fun loginWithGoogle(idToken: String, nome: String, email: String, dataNascimento: String? = "") {
        viewModelScope.launch {
            _loginState.value = LoginState.Loading
            _uiState.update { it.copy(isLoading = true) }
            val result = loginWithGoogleUseCase(idToken)
            if (result.isSuccess) {
                handleGoogleSignIn(nome, email, dataNascimento)
            } else {
                _uiState.update { it.copy(errorMessage = result.exceptionOrNull()?.message, isLoading = false) }
            }
        }
    }

    private fun handleSignInResult(task: Task<GoogleSignInAccount>) {
        try {
            val googleAccount = task.getResult(ApiException::class.java)
            googleAccount?.idToken?.let { idToken ->
                val displayName = googleAccount.displayName ?: ""
                val email = googleAccount.email ?: ""
                loginWithGoogle(idToken, displayName, email)
            }
        } catch (e: ApiException) {
            _uiState.update { it.copy(errorMessage = e.message) }
        }
    }

    fun handleGoogleSignInResult(result: ActivityResult) {
        if (result.resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            if (task.isSuccessful) {
                handleSignInResult(task)
            } else {
                task.exception?.printStackTrace()
            }
        } else {
            Log.e("GoogleSignIn", "Resultado cancelado ou falha: ${result.resultCode}")
        }
    }

    private fun verifyFields(email: String, password: String): Boolean {
        val isEmailError = !isValidEmail(email)
        val isPasswordError = !isValidPassword(password)
        return isEmailError || isPasswordError
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
    object Logged : LoginState()
    data class Error(val message: String) : LoginState()
}

fun isValidEmail(email: String): Boolean {
    val emailRegex = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}\$".toRegex()
    return emailRegex.matches(email)
}

fun isValidPassword(password: String): Boolean {
    return password.length >= 6
}

