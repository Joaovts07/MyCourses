package com.example.login.viewmodels

import android.app.Activity
import android.util.Log
import androidx.activity.result.ActivityResult
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.login.repository.AuthRepository
import com.example.login.validators.isValidEmail
import com.example.login.validators.isValidPassword
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
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
        if(verifyFields(uiState.email, uiState.password)) {
            uiState.copy(errorMessage = "Campos inválidos", isLoading = false)
            _loginState.value = LoginState.Error("Campos inválidos")
            return
        }
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

    private fun handleGoogleSignIn(nome: String, email: String, dataNascimento: String? = "") {
        viewModelScope.launch {
            _loginState.value = LoginState.Loading
            uiState = uiState.copy(isLoading = true)
            val userExists = authRepository.checkIfUserExists()
            if (!userExists) {
                val dateBirthdayTimestamp = try {
                    val formatter = SimpleDateFormat("dd/MM/yyyy", Locale("pt", "BR"))
                    val dateBirthday = dataNascimento?.let { formatter.parse(it) }
                    dateBirthday?.let { Timestamp(it) }
                    } catch (ex: Exception) {
                        Timestamp.now()
                    }
                authRepository.createUser(nome, email, dateBirthdayTimestamp)
            }
            _loginState.value = LoginState.Logged
            uiState.copy(isLoading = false)
        }
    }

    fun loginWithGoogle(idToken: String, nome: String, email: String, dataNascimento: String? = "") {
        viewModelScope.launch {
            _loginState.value = LoginState.Loading
            uiState = uiState.copy(isLoading = true)
            val result = authRepository.loginWithGoogle(idToken)
            if (result.isSuccess) {
                handleGoogleSignIn(nome, email, dataNascimento)
            } else {
                uiState.copy(errorMessage = result.exceptionOrNull()?.message, isLoading = false)
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
            uiState.copy(errorMessage = e.message)
        }
    }

    fun handleGoogleSignInResult(
        result: ActivityResult,
    ) {
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

    private fun verifyFields(
        email: String,
        password: String
    ): Boolean {
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
