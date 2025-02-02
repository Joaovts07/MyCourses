package com.example.mycourses
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.compose.rememberNavController
import com.example.login.firebase.auth
import com.example.login.ui.LoginNavigation
import com.example.mycourses.navigation.AppNavigation
import com.example.mycourses.ui.theme.MyCoursesTheme
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private lateinit var authStateListener: FirebaseAuth.AuthStateListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyCoursesTheme {
                val navController = rememberNavController()
                val auth = FirebaseAuth.getInstance()
                var isUserAuthenticated by remember { mutableStateOf(false) }

                LaunchedEffect(auth) {
                    isUserAuthenticated = auth.currentUser != null
                }

                if (isUserAuthenticated) {
                    AppNavigation(navController)
                } else {
                    LoginNavigation(
                        navController = navController,
                        routeSuccess = "home",
                        onLoginSuccess = {
                            isUserAuthenticated = true
                        }
                    )
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        auth.removeAuthStateListener(authStateListener)
    }
}
