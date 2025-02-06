package com.example.mycourses

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.login.firebase.auth
import com.example.login.ui.LoginNavigation
import com.example.mycourses.navigation.AppNavigation
import com.example.mycourses.ui.theme.MyCoursesTheme
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyCoursesTheme {
                val navController = rememberNavController()
                val auth = FirebaseAuth.getInstance()
                var authState by remember { mutableStateOf(AuthState.LOADING) }
                LaunchedEffect(auth) {
                    authState = if (auth.currentUser != null) AuthState.AUTHENTICATED else AuthState.UNAUTHENTICATED
                }

                when (authState) {
                    AuthState.LOADING -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                    AuthState.AUTHENTICATED -> {
                        AppNavigation(navController)
                    }
                    AuthState.UNAUTHENTICATED -> {
                        LoginNavigation(
                            navController = navController,
                            routeSuccess = "home",
                            onLoginSuccess = {
                                authState = AuthState.AUTHENTICATED
                            }
                        )
                    }
                }
            }
        }
    }



}

enum class AuthState {
    LOADING, AUTHENTICATED, UNAUTHENTICATED
}