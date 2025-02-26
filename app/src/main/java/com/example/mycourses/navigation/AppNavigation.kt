package com.example.mycourses.navigation

import AccountScreen
import EditAccountScreen
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import com.example.login.ui.screens.LoginScreen
import com.example.mycourses.ui.components.MyCoursesScaffold
import com.example.mycourses.ui.screens.*
import com.example.mycourses.ui.screens.createCourse.CourseImageScreen
import com.example.mycourses.ui.screens.createCourse.CourseInfoScreen
import com.example.mycourses.ui.screens.createCourse.CourseReviewScreen
import com.example.mycourses.viewmodels.AccountViewModel
import com.example.mycourses.viewmodels.CourseCreationViewModel

@Composable
fun AppNavigation(navController: NavHostController) {
    val accountViewModel: AccountViewModel = hiltViewModel()
    val courseCreationViewModel: CourseCreationViewModel = hiltViewModel()
    val backStackEntryState by navController.currentBackStackEntryAsState()
    val currentDestination = backStackEntryState?.destination
    val selectedItem by remember(currentDestination) {
        mutableStateOf(
            bottomAppBarItems.find { it.destination.route == currentDestination?.route }
                ?: bottomAppBarItems.first()
        )
    }
    NavHost(
        navController = navController,
        startDestination = AppDestination.Home.route
    ) {
        composable(AppDestination.Home.route) {
            HomeScreen(navController)
        }

        composable(
            "${AppDestination.CourseDetails.route}/{courseId}",
            arguments = listOf(navArgument("courseId") { type = NavType.StringType })
        ) { backStackEntry ->
            val courseId = backStackEntry.arguments?.getString("courseId") ?: ""
            CourseDetailsScreen(courseId){
                navController.navigate("${AppDestination.CourseInfoCreation.route}/$courseId")
            }

        }
        composable(AppDestination.FavoriteCourses.route) {
            MyCoursesScaffold(navController, selectedItem) { paddingValues ->
                Box(modifier = Modifier.padding(paddingValues)) {
                    CourseFavoriteScreen(
                        onNavigateToDetails = { courseId ->
                            navController.navigate("${AppDestination.CourseDetails.route}/$courseId")
                        }
                    )
                }
            }
        }
        composable(AppDestination.EditAccount.route) {
            EditAccountScreen(navController, accountViewModel )
        }
        composable(AppDestination.Account.route) {
            MyCoursesScaffold(navController, selectedItem) { paddingValues ->
                Box(modifier = Modifier.padding(paddingValues)) {
                    AccountScreen(
                        navController = navController,
                        onEditClick = {
                            navController.navigate(AppDestination.EditAccount.route)
                        },
                        onLogout = {
                            navController.navigate("login")

                        }
                    )
                }
            }
        }
        composable(AppDestination.UploadUserProfile.route) {
            UploadPhotoScreen(accountViewModel, navController)
        }
        composable("login") {
            LoginScreen(
                navController = navController,
                onLoginSuccess = {
                    navController.navigate(AppDestination.Account.route) {
                        popUpTo("login") { inclusive = true }
                    }
                },
                context = LocalContext.current
            )
        }
        composable("${AppDestination.CourseInfoCreation.route}/{courseId}",
            arguments = listOf(navArgument("courseId") { type = NavType.StringType; nullable = true })
        ) { backStackEntry ->
            val courseId = backStackEntry.arguments?.getString("courseId")
            MyCoursesScaffold(navController, selectedItem, false) { paddingValues ->
                Box(modifier = Modifier.padding(paddingValues)) {
                    CourseInfoScreen(
                        courseId = courseId,
                        courseCreationViewModel = courseCreationViewModel,
                        onNext = { navController.navigate(AppDestination.CourseImageCreation.route) },
                    )
                }
            }
        }

        composable(AppDestination.CourseImageCreation.route) {
            CourseImageScreen(
                viewModel = courseCreationViewModel,
                onNext = { navController.navigate(AppDestination.CourseReviewCreation.route) },
                onBack = { navController.popBackStack() }
            )
        }

        composable(AppDestination.CourseReviewCreation.route) {
            CourseReviewScreen(
                viewModel = courseCreationViewModel,
                onBack = { navController.popBackStack() }
            )
        }
    }
}

