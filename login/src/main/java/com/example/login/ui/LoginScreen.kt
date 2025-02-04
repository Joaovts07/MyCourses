package com.example.login.ui

import RegistrationBasicScreen
import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.login.BuildConfig
import com.example.login.firebase.auth
import com.example.login.ui.components.EmailInput
import com.example.login.ui.components.GoogleSignInButton
import com.example.login.ui.components.LoadingButton
import com.example.login.ui.components.PasswordInput
import com.example.mylogin.ui.theme.MyLoginTheme
import com.example.login.validators.isValidEmail
import com.example.login.validators.isValidPassword
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(navController: NavHostController, context: Context, onLoginSuccess: () -> Unit) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showError by remember { mutableStateOf(false) }
    var showSnackbar by remember { mutableStateOf(false) }
    var snackbarMessage by remember { mutableStateOf("") }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            if (task.isSuccessful) {
                handleSignInResult(task, onLoginSuccess)
            } else {
                Log.e("GoogleSignIn", "Falha ao obter conta: ${task.exception?.message}")
                task.exception?.printStackTrace()
            }
        } else {
            Log.e("GoogleSignIn", "Resultado cancelado ou falha: ${result.resultCode}")
        }
    }

    fun signInWithEmailAndPassword(email: String, password: String, onResult: (Boolean,Boolean?) -> Unit) {
        val auth: FirebaseAuth = Firebase.auth
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onResult(true, task.result.user?.isEmailVerified)
                }
            }
            .addOnFailureListener { exception ->
                if (exception is FirebaseAuthInvalidCredentialsException || exception is FirebaseAuthException) {
                    snackbarMessage = when (exception.message) {
                        "ERROR_USER_NOT_FOUND" -> "Usuário não encontrado."
                        "ERROR_WRONG_PASSWORD" -> "Senha incorreta."
                        "ERROR_INVALID_EMAIL" -> "Email inválido."
                        else -> "Erro ao fazer login: ${exception.message}"
                    }
                } else {
                    snackbarMessage = "Erro ao fazer login: ${exception.message}"
                }
                onResult(false, false)
            }
    }

    Scaffold(
        snackbarHost = {
            if (showSnackbar) {
                Snackbar { Text(snackbarMessage) }
            }
        },
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Login") }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
        ) {
            EmailInput(
                email = email,
                onEmailChange = { email = it },
            )
            Spacer(modifier = Modifier.height(16.dp))

            PasswordInput(
                password = password,
                onPasswordChange = { password = it },
            )
            Spacer(modifier = Modifier.height(24.dp))

            if (showError) {
                Text(
                    "Email ou senha inválidos",
                    color = MaterialTheme.colorScheme.error

                )
            }
            var isLoading by remember { mutableStateOf(false) }
            LoadingButton(
                onClick = {
                    isLoading = true
                    val fields = verifyFields(email, password)
                    if (fields) {
                        isLoading = false
                        showError = true
                        return@LoadingButton
                    }
                    signInWithEmailAndPassword(email, password) { success, emailVerified ->
                        if (success) {
                            if (emailVerified == true) {
                                showSnackbar = true
                                showError = false
                                onLoginSuccess()

                            } else {
                                val verificationId = ""
                                auth.currentUser?.sendEmailVerification()
                                navController.navigate("confirmationScreen/email/${email}/${verificationId}")
                            }

                        } else {
                            showSnackbar = true
                            isLoading = false
                        }
                    }


                },
                isLoading = isLoading,
                text = "Login",
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            )
            Spacer(modifier = Modifier.height(18.dp))

            GoogleSignInButton(onClick = {
                val googleClientId = BuildConfig.GOOGLE_CLIENT_ID
                val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(googleClientId)
                    .requestEmail()
                    .build()
                val googleSignInClient = GoogleSignIn.getClient(context, gso)
                launcher.launch(googleSignInClient.signInIntent)
            })

            Spacer(modifier = Modifier.height(18.dp))

            Text(
                text = "Não tem uma conta? Cadastre-se",
                modifier = Modifier
                    .clickable { navController.navigate("basicForm") }
                    .padding(top = 8.dp),
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}
private fun handleSignInResult(task: Task<GoogleSignInAccount>, onLoginSuccess: () -> Unit) {
    try {
        val account = task.getResult(ApiException::class.java)
        account?.idToken?.let { idToken ->
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            Firebase.auth.signInWithCredential(credential)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        onLoginSuccess()
                    } else {
                        // Tratar erro
                    }
                }
        }
    } catch (e: ApiException) {
        // Tratar erro
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

@Composable
fun LoginNavigation(navController: NavHostController,routeSuccess: String, onLoginSuccess: @Composable () -> Unit) {
    MyLoginTheme {
        NavHost(navController = navController, startDestination = "login") {
            composable(routeSuccess) {
                onLoginSuccess()
            }
            composable("login") {
                LoginScreen(
                    navController = navController,
                    onLoginSuccess = {
                        navController.navigate(routeSuccess) {
                            popUpTo("login") { inclusive = true } // Remover a tela de login da pilha
                        }
                    },
                    context = LocalContext.current
                )
            }
            composable("basicForm") { RegistrationBasicScreen(navController) }
            composable(
                "choiseForm/{nome}/{dataNascimento}",
                arguments = listOf(
                    navArgument("nome") { type = NavType.StringType },
                    navArgument("dataNascimento") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                RegistrationChoiseScreen(
                    navController,
                    backStackEntry.arguments?.getString("nome") ?: "",
                    backStackEntry.arguments?.getString("dataNascimento") ?: ""
                )

            }
            composable("confirmationScreen/{verificationType}/{id}/{verificationId}") { backStackEntry ->
                val verificationType = backStackEntry.arguments?.getString("verificationType") ?: "email"
                val id = backStackEntry.arguments?.getString("id") ?: ""
                val verificationId = backStackEntry.arguments?.getString("verificationId") ?: ""
                ConfirmationScreen(
                    navController,
                    verificationType = verificationType,
                    id = id,
                    verificationId = verificationId
                )
            }
        }
    }
}
