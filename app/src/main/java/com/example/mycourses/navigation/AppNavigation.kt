package com.example.mycourses.navigation

import AccountScreen
import EditAccountScreen
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.mycourses.model.entities.Course
import com.example.mycourses.model.entities.deserializeUser
import com.example.mycourses.model.entities.serializeUser
import com.example.mycourses.ui.screens.*
import com.example.mycourses.viewmodels.AccountViewModel
import com.example.mycourses.viewmodels.CoursesListViewModel

@Composable
fun AppNavigation(navController: NavHostController) {
    val accountViewModel: AccountViewModel = hiltViewModel()
    val coursesListViewModel: CoursesListViewModel = hiltViewModel()

    NavHost(
        navController = navController,
        startDestination = AppDestination.Home.route
    ) {
        composable(AppDestination.Home.route) {
            HomeScreen(navController, coursesListViewModel)
        }
        composable(AppDestination.Highlight.route) {
            CoursesListScreen(
                onNavigateToDetails = { course ->
                    navController.navigate("${AppDestination.CourseDetails.route}/$course")
                },
                viewModel = coursesListViewModel
            )
        }
        composable(
            "${AppDestination.CourseDetails.route}/{course}",
            arguments = listOf(navArgument("course") { type = NavType.StringType })
        ) { backStackEntry ->
            val course = backStackEntry.arguments?.getParcelable<Course>("course")
            CourseDetailsScreen(
                course = course,
                onNavigateToCheckout = {}
            )
        }
        composable(AppDestination.FavoriteCourses.route) {
            CourseFavoriteScreen()
        }
        composable("${AppDestination.EditAccount.route}/{userJson}",
            arguments = listOf(navArgument("userJson") { type = NavType.StringType })
        ) { backStackEntry ->
            val userJson = backStackEntry.arguments?.getString("userJson") ?: ""
            val user = deserializeUser(userJson)
            EditAccountScreen(navController, accountViewModel )
        }
        composable(AppDestination.Account.route) {
            AccountScreen(
                onEditClick = { user ->
                    val userJson = serializeUser(user)
                    navController.navigate("${AppDestination.EditAccount.route}/$userJson")
                }
            )
        }
        composable(AppDestination.UploadUserProfile.route) {
            UploadPhotoScreen(accountViewModel, navController)
        }
    }
}
