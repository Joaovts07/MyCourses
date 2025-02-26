package com.example.mycourses.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.mycourses.navigation.AppDestination
import com.example.mycourses.navigation.bottomAppBarItems
import com.example.mycourses.ui.components.MyCoursesScaffold


@Composable
fun HomeScreen(navController: NavHostController) {
    val backStackEntryState by navController.currentBackStackEntryAsState()
    val currentDestination = backStackEntryState?.destination
    val selectedItem by remember(currentDestination) {
        mutableStateOf(
            bottomAppBarItems.find { it.destination.route == currentDestination?.route }
                ?: bottomAppBarItems.first()
        )
    }
    MyCoursesScaffold(navController, selectedItem) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            CoursesListScreen(
                onNavigateToDetails = { courseId ->
                    navController.navigate("${AppDestination.CourseDetails.route}/$courseId")
                }
            )
        }
    }


}