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
import com.example.mycourses.model.entities.EnrolledCourse
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
            "${AppDestination.CourseDetails.route}/{enrolledCourse}",
            arguments = listOf(navArgument("enrolledCourse") { type = NavType.StringType })
        ) { backStackEntry ->
            val enrolledCourseJson = backStackEntry.arguments?.getString("enrolledCourse") ?: ""
            val decodedJson = URLDecoder.decode(enrolledCourseJson, "UTF-8")
            val enrolledCourse = Gson().fromJson(decodedJson, EnrolledCourse::class.java)

            CourseDetailsScreen(
                enrolledCourse = enrolledCourse,
                onNavigateToCheckout = {}
            )
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
