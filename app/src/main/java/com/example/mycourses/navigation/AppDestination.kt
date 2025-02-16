package com.example.mycourses.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.outlined.Star
import com.example.mycourses.ui.components.BottomAppBarItem


sealed class AppDestination(val route: String) {
    data object Home : AppDestination("initialScreen")
    data object Highlight : AppDestination("courses")
    data object MyCourses : AppDestination("mycourses")
    data object Account : AppDestination("account")
    data object CourseDetails : AppDestination("courseDetails")
    data object FavoriteCourses: AppDestination("favoriteCourses")
    data object EditAccount: AppDestination("editAccount")
    data object UploadUserProfile: AppDestination("uploadPhoto")
    data object CourseInfoCreation: AppDestination("courseInfoCreation")
    data object CourseImageCreation: AppDestination("courseImageCreation")
    data object CourseReviewCreation: AppDestination("courseReviewCreation")

}

val bottomAppBarItems = listOf(
    BottomAppBarItem(
        label = "Cursos",
        icon = Icons.Filled.DateRange,
        destination = AppDestination.Highlight
    ),
    BottomAppBarItem(
        label = "Minha Conta",
        icon = Icons.Filled.AccountCircle,
        destination = AppDestination.Account
    ),
    BottomAppBarItem(
        label = "Favoritos",
        icon = Icons.Outlined.Star,
        destination = AppDestination.FavoriteCourses
    ),
)