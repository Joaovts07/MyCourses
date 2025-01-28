package com.example.mycourses
import AccountScreen
import EditAccountScreen
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.login.firebase.auth
import com.example.login.ui.LoginNavigation
import com.example.login.ui.LoginScreen
import com.example.mycourses.model.entities.Course
import com.example.mycourses.model.entities.CourseType
import com.example.mycourses.model.entities.serializeUser
import com.example.mycourses.navigation.AppDestination
import com.example.mycourses.navigation.bottomAppBarItems
import com.example.mycourses.ui.screens.CoursesListScreen
import com.example.mycourses.ui.components.BottomAppBarItem
import com.example.mycourses.ui.components.MyCoursesBottomAppBar
import com.example.mycourses.ui.screens.CourseDetailsScreen
import com.example.mycourses.ui.screens.CourseFavoriteScreen
import com.example.mycourses.ui.screens.UploadPhotoScreen
import com.example.mycourses.ui.theme.MyCoursesTheme
import com.example.mycourses.viewmodels.AccountViewModel
import com.example.mycourses.viewmodels.CoursesListViewModel
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

                var navigationState by rememberSaveable { mutableStateOf("CHECK_AUTH") }

                LaunchedEffect(auth) {
                    val user = auth.currentUser
                    navigationState = if (user != null) "INITIAL" else "LOGIN"
                }

                when (navigationState) {
                    "LOGIN" -> {
                        LoginNavigation(
                            navController = navController,
                            routeSuccess = AppDestination.Highlight.route,
                            onLoginSuccess = {
                                navigationState = "INITIAL"
                            }
                        )
                    }
                    "INITIAL" -> {
                        LaunchedEffect(Unit) {
                            navController.navigate(AppDestination.Highlight.route) {
                                popUpTo(0) { inclusive = true }
                            }
                        }
                        LoginToInitial(navController)
                    }
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
    val accountViewModel: AccountViewModel = hiltViewModel()
    val coursesListViewModel: CoursesListViewModel = hiltViewModel()
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
    MyCoursesApp(
        bottomAppBarItemSelected = selectedItem,
        onBottomAppBarItemSelectedChange = {
            val route = it.destination.route
            navController.navigate(route) {
                launchSingleTop = true
                popUpTo(route)
            }
        },
        onFabClick = {},
        isShowTopBar = containsInBottomAppBarItems,
        isShowBottomBar = containsInBottomAppBarItems,
        isShowFab = isShowFab
    ) {
        NavHost(
            navController = navController,
            startDestination = AppDestination.Highlight.route
        ) {

            composable("login") {
                LoginScreen(
                    navController = navController,
                    onLoginSuccess = {
                        navController.navigate(AppDestination.Highlight.route) {
                            popUpTo("login") {
                                inclusive = true
                            }
                        }
                    }
                )
            }

            composable(AppDestination.Highlight.route) {
                Log.d("CoursesListScreen", "Instanciando ViewModel no NavHost")
                CoursesListScreen(
                    onNavigateToDetails = { course ->
                        navController.navigate(
                            "${AppDestination.CourseDetails.route}/$course"
                        )
                    },
                    viewModel = coursesListViewModel
                )
            }
            composable(
                "${AppDestination.CourseDetails.route}/{course}",
                arguments = listOf(navArgument("course") { type = CourseType() })
            ) { backStackEntry ->
                val course = backStackEntry.arguments?.getParcelable<Course>("course")
                CourseDetailsScreen(
                    course = course,
                    onNavigateToCheckout = {
                        //navController.navigate(AppDestination.Checkout.route)
                    },
                )

            }
            composable(AppDestination.FavoriteCourses.route) {
                CourseFavoriteScreen()
            }
            composable("${AppDestination.EditAccount.route}/{userJson}",
                arguments = listOf(navArgument("userJson") {type = NavType.StringType} )
            ) {
                EditAccountScreen(
                    navController = navController,
                    viewModel = accountViewModel,
                )
            }
            composable(AppDestination.Account.route) {
                NavigateToAccountScreen(navController)
            }
            composable(AppDestination.UploadUserProfile.route) {
                UploadPhotoScreen(accountViewModel, navController)
            }
        }

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyCoursesApp(
    bottomAppBarItemSelected: BottomAppBarItem = bottomAppBarItems.first(),
    onBottomAppBarItemSelectedChange: (BottomAppBarItem) -> Unit = {},
    onFabClick: () -> Unit = {},
    isShowTopBar: Boolean = false,
    isShowBottomBar: Boolean = false,
    isShowFab: Boolean = false,
    content: @Composable () -> Unit
) {
    Scaffold(
        topBar = {
            if (isShowTopBar) {
                CenterAlignedTopAppBar(
                    title = {
                        Text(text = "My Courses")
                    },
                )
            }
        },
        bottomBar = {
            if (isShowBottomBar) {
                MyCoursesBottomAppBar(
                    item = bottomAppBarItemSelected,
                    items = bottomAppBarItems,
                    onItemChange = onBottomAppBarItemSelectedChange,
                )
            }
        },
        floatingActionButton = {
            if (isShowFab) {
                FloatingActionButton(
                    onClick = onFabClick
                ) {
                    Icon(
                        Icons.Filled.Edit,
                        contentDescription = null
                    )
                }
            }
        }
    ) {
        Box(
            modifier = Modifier.padding(it)
        ) {
            content()
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MyCoursesTheme {
         Greeting("Android")
    }
}



@Composable
private fun NavigateToAccountScreen(navController: NavHostController) {
    AccountScreen(
        onEditClick = { user ->
            val userJson = serializeUser(user)
            navController.navigate("${AppDestination.EditAccount.route}/$userJson")

        }
    )
}
