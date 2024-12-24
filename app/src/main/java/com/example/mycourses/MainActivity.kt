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
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.login.LoginNavigation
import com.example.login.firebase.auth
import com.example.mycourses.navigation.AppDestination
import com.example.mycourses.navigation.bottomAppBarItems
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
    val backStackEntryState by navController.currentBackStackEntryAsState()
    val currentDestination = backStackEntryState?.destination
    val selectedItem by remember(currentDestination) {
        val item = currentDestination?.let { destination ->
            bottomAppBarItems.find {
                it.destination.route == destination.route
            }
        } ?: bottomAppBarItems.first()
        mutableStateOf(item)
    }
    val containsInBottomAppBarItems = currentDestination?.let { destination ->
        bottomAppBarItems.find {
            it.destination.route == destination.route
        }
    } != null
    val isShowFab = when (currentDestination?.route) {
        AppDestination.Account.route,
        AppDestination.MyCourses.route -> true
        else -> false
    }

}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MyCoursesTheme {
         Greeting("Android")
    }
} 