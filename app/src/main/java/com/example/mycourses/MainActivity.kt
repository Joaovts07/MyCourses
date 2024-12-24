package com.example.mycourses

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.login.LoginNavigation
import com.example.login.firebase.auth
import com.example.mycourses.ui.Conta
import com.example.mycourses.ui.MeusCursos
import com.example.mycourses.ui.Principal
import com.example.mycourses.ui.TelaInicial
import com.example.mycourses.ui.Telas
import com.example.mycourses.ui.theme.MyCoursesTheme
import com.google.firebase.auth.FirebaseAuth

class MainActivity : ComponentActivity() {
    private lateinit var authStateListener: FirebaseAuth.AuthStateListener
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyCoursesTheme {
                val navController = rememberNavController()
                val auth = FirebaseAuth.getInstance()
                var shouldNavigateToLogin by remember { mutableStateOf(false) }
                var shouldNavigateToInitial by remember { mutableStateOf(false) }

                auth.addAuthStateListener { firebaseAuth ->
                    val user = firebaseAuth.currentUser
                    if (user != null) {
                        shouldNavigateToInitial = true
                    } else {
                        shouldNavigateToLogin = true

                    }
                }
                if (shouldNavigateToLogin) {
                    LoginNavigation(navController)
                } else if (shouldNavigateToInitial) {
                    LoginToInitial(navController)
                }

            }

        }
    }

    override fun onDestroy() {
        super.onDestroy()
        auth.removeAuthStateListener(authStateListener)
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}
@Composable
fun LoginToInitial(navController: NavHostController) {
    val paddingValues = remember { PaddingValues(0.dp) }
    NavHost(navController = navController, startDestination = "initialScreen") {
        composable("initialScreen") {
            TelaInicial(navController)
        }
        composable(Telas.MEUS_CURSOS.rota) { MeusCursos(paddingValues) }
        composable(Telas.PRINCIPAL.rota) { Principal(paddingValues) }
        composable(Telas.CONTA.rota) { Conta(paddingValues) }

    }

}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MyCoursesTheme {
         Greeting("Android")
    }
} 