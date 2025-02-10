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
import com.example.mycourses.ui.screens.*
import com.example.mycourses.viewmodels.AccountViewModel
import com.example.mycourses.viewmodels.CoursesListViewModel
import com.google.gson.Gson
import java.net.URLDecoder
import java.net.URLEncoder

@Composable
fun AppNavigation(navController: NavHostController) {
    val accountViewModel: AccountViewModel = hiltViewModel()
    val coursesListViewModel: CoursesListViewModel = hiltViewModel()

    NavHost(
        navController = navController,
        startDestination = AppDestination.Home.route
    ) {
        composable(AppDestination.Home.route) {
            HomeScreen(navController)
        }
        composable(AppDestination.Highlight.route) {
            CoursesListScreen(
                onNavigateToDetails = { course ->
                    val courseJson = URLEncoder.encode(Gson().toJson(course), "UTF-8")
                    navController.navigate("${AppDestination.CourseDetails.route}/$courseJson")
                },
                viewModel = coursesListViewModel
            )
        }
        composable(
            "${AppDestination.CourseDetails.route}/{course}",
            arguments = listOf(navArgument("course") { type = NavType.StringType })
        ) { backStackEntry ->
            val courseJson = backStackEntry.arguments?.getString("course") ?: ""
            val decodedJson = URLDecoder.decode(courseJson, "UTF-8")
            val course = Gson().fromJson(decodedJson, Course::class.java)
            CourseDetailsScreen(course = course)
        }
        composable(AppDestination.FavoriteCourses.route) {
            CourseFavoriteScreen()
        }
        composable(AppDestination.EditAccount.route) {
            EditAccountScreen(navController, accountViewModel )
        }
        composable(AppDestination.Account.route) {
            AccountScreen(
                onEditClick = {
                    navController.navigate(AppDestination.EditAccount.route)
                },
                onCourseClicked = { course ->
                    val courseJson = URLEncoder.encode(Gson().toJson(course), "UTF-8")
                    navController.navigate("${AppDestination.CourseDetails.route}/$courseJson")
                }
            )
        }
        composable(AppDestination.UploadUserProfile.route) {
            UploadPhotoScreen(accountViewModel, navController)
        }
    }
}
