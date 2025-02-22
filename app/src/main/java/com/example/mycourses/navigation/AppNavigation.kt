package com.example.mycourses.navigation

import AccountScreen
import EditAccountScreen
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.login.ui.screens.LoginScreen
import com.example.mycourses.ui.screens.*
import com.example.mycourses.ui.screens.createCourse.CourseImageScreen
import com.example.mycourses.ui.screens.createCourse.CourseInfoScreen
import com.example.mycourses.ui.screens.createCourse.CourseReviewScreen
import com.example.mycourses.viewmodels.AccountViewModel
import com.example.mycourses.viewmodels.CourseCreationViewModel
import com.example.mycourses.viewmodels.CoursesListViewModel


@Composable
fun AppNavigation(navController: NavHostController) {
    val accountViewModel: AccountViewModel = hiltViewModel()
    val coursesListViewModel: CoursesListViewModel = hiltViewModel()
    val courseCreationViewModel: CourseCreationViewModel = hiltViewModel()

    NavHost(
        navController = navController,
        startDestination = AppDestination.Home.route
    ) {
        composable(AppDestination.Home.route) {
            HomeScreen(navController)
        }
        composable(AppDestination.Highlight.route) {
            CoursesListScreen(
                onNavigateToDetails = { courseId ->
                    navController.navigate("${AppDestination.CourseDetails.route}/courseId")
                },
                viewModel = coursesListViewModel
            )
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
            CourseFavoriteScreen(
                onNavigateToDetails = { courseId ->
                    navController.navigate("${AppDestination.CourseDetails.route}/${courseId}")
                }
            )
        }
        composable(AppDestination.EditAccount.route) {
            EditAccountScreen(navController, accountViewModel )
        }
        composable(AppDestination.Account.route) {
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
        composable(AppDestination.UploadUserProfile.route) {
            UploadPhotoScreen(accountViewModel, navController)
        }
        composable("login") {
            LoginScreen(
                navController = navController,
                onLoginSuccess = {
                    navController.navigate("login") {
                        popUpTo(AppDestination.Account.route) { inclusive = true }
                    }
                },
                context = LocalContext.current
            )
        }
        composable("${AppDestination.CourseInfoCreation.route}/{courseId}",
            arguments = listOf(navArgument("courseId") { type = NavType.StringType; nullable = true })
        ) { backStackEntry ->
            val courseId = backStackEntry.arguments?.getString("courseId")
            CourseInfoScreen(
                courseId = courseId,
                courseCreationViewModel = courseCreationViewModel,
                onNext = { navController.navigate(AppDestination.CourseImageCreation.route) },
                onBack = { navController.popBackStack() }
            )
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

