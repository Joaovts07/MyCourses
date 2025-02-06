package com.example.login.ui

import android.app.Activity
import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.login.firebase.PhoneAuthState
import com.example.login.firebase.PhoneAuthentication
import com.example.login.firebase.auth
import com.example.login.ui.components.EmailInput
import com.example.login.ui.components.PasswordInput
import com.example.login.validators.PhoneNumberMaskTransformation
import com.example.login.validators.isValidEmail
import com.example.login.validators.isValidPassword
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistrationChoiseScreen(navController: NavController, nome: String, dataNascimento: String) {
    var method by remember { mutableStateOf("email") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var showSnackbar by remember { mutableStateOf(false) }
    var snackbarMessage by remember { mutableStateOf("") }
    var showPhoneAuthentication by remember { mutableStateOf(false) }
    val auth: FirebaseAuth = Firebase.auth
    val context = LocalContext.current
    val activity = context as Activity

    Scaffold(
        snackbarHost = {
            if (showSnackbar) {
                Snackbar { Text(snackbarMessage) }
            }
        },
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Cadastro - Passo 2") }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            Text("Escolha o método de validação:")
            Spacer(modifier = Modifier.height(16.dp))

            Row {
                RadioButton(
                    selected = method == "email",
                    onClick = { method = "email" }
                )
                Text("Email")
                Spacer(modifier = Modifier.width(16.dp))

                RadioButton(
                    selected = method == "telefone",
                    onClick = { method = "telefone" }
                )
                Text("Telefone")
            }
            Spacer(modifier = Modifier.height(24.dp))


            if (method == "email") {
                EmailInput(
                    email = email,
                    onEmailChange = { email = it },
                )
                PasswordInput(
                    password = password,
                    onPasswordChange = { password = it },
                )

            } else {
                val phoneNumberError by remember { mutableStateOf(false) }

                OutlinedTextField(
                    value = phoneNumber,
                    onValueChange = {
                        if (it.length <= 11) {
                            phoneNumber = it.filter { it.isDigit() }
                        }
                    },
                    label = { Text("Numero de telefone") },

                    modifier = Modifier.fillMaxWidth(),
                    visualTransformation = PhoneNumberMaskTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    isError = phoneNumberError,
                    supportingText = {
                        if (phoneNumberError) {
                            Text("Número de telefone invalido")
                        }
                    }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = {
                    if (method == "email") {
                        if (validateWithEmail(email, password)) return@Button
                        auth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    createUser(nome, email, dataNascimento)
                                    auth.currentUser?.sendEmailVerification()
                                        ?.addOnCompleteListener { verificacaoTask ->
                                            if (verificacaoTask.isSuccessful) {
                                                navController.navigate("confirmationScreen/${method}/${email}/")
                                            }
                                        }
                                } else {
                                    showSnackbar = true
                                    snackbarMessage = "Falha no cadastro: ${task.exception?.message}"
                                }
                            }
                    } else {
                        showPhoneAuthentication = true
                    }

                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text("Concluir Cadastro")
            }

        }

    }
    if (showPhoneAuthentication) {
        val formatedPhone = "+55${phoneNumber.filter { it.isDigit() }}"
        PhoneAuthentication(activity, formatedPhone) { state ->
            when (state) {
                is PhoneAuthState.CodeSent -> {
                    val verificationId = state.verificationId
                    navController.navigate("confirmationScreen/sms/${phoneNumber}/${verificationId}")
                    showPhoneAuthentication = false
                }
                is PhoneAuthState.Error -> {
                    showPhoneAuthentication = false
                    snackbarMessage = state.message
                }
                PhoneAuthState.Default -> { }
                PhoneAuthState.Loading -> { }
                is PhoneAuthState.Success -> {
                    snackbarMessage = "Login bem sucedido"
                }
            }
        }
    }

}

private fun validateWithEmail(
    email: String,
    password: String
): Boolean {
    val isEmailError1: Boolean = !isValidEmail(email)
    val isPasswordError1: Boolean = !isValidPassword(password)
    return isEmailError1 || isPasswordError1
}

fun createUser(nome: String, email: String, dataNascimento: String) {
    val db = Firebase.firestore
    val userId = auth.currentUser?.uid ?: return
    val usersRef = db.collection("users").document(userId)
    val datebirthdayTimestamp = try {
        val formateData = SimpleDateFormat("dd/MM/yyyy", Locale("pt", "BR"))
        val dateBirthday = formateData.parse(dataNascimento)
        dateBirthday?.let { Timestamp(it) }
    } catch (ex: Exception) {
        ""
    }

    val newUser = hashMapOf(
        "id" to userId,
        "name" to nome,
        "email" to email,
        "birthday" to datebirthdayTimestamp
    )
    usersRef.set(newUser)
        .addOnSuccessListener {
            Log.d("TAG", "Documento adicionado com ID: $userId")
        }
        .addOnFailureListener { e ->
            Log.w("TAG", "Erro ao adicionar documento", e)
        }
}


fun checkIfUserExists(userId: String, onResult: (Boolean) -> Unit) {
    val db = FirebaseFirestore.getInstance()
    db.collection("users")
        .document(userId)
        .get()
        .addOnSuccessListener { document ->
            onResult(document.exists())
        }
        .addOnFailureListener { e ->
            Log.e("Firestore", "Erro ao verificar usuário: ${e.message}")
            onResult(false)
        }
}





