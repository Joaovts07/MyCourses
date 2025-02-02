package com.example.mycourses.ui.screens

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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.mycourses.navigation.AppDestination
import com.example.mycourses.navigation.bottomAppBarItems
import com.example.mycourses.ui.components.MyCoursesBottomAppBar
import com.google.gson.Gson
import java.net.URLEncoder

@OptIn(ExperimentalMaterial3Api::class)
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

    val isShowFab = currentDestination?.route in listOf(
        AppDestination.Account.route,
        AppDestination.MyCourses.route
    )

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("My Courses") }
            )
        },
        bottomBar = {
            MyCoursesBottomAppBar(
                item = selectedItem,
                items = bottomAppBarItems,
                onItemChange = { navController.navigate(it.destination.route) }
            )
        },
        floatingActionButton = {
            if (isShowFab) {
                FloatingActionButton(onClick = {}) {
                    Icon(Icons.Filled.Edit, contentDescription = null)
                }
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            CoursesListScreen(
                onNavigateToDetails = { course ->
                    val courseJson = URLEncoder.encode(Gson().toJson(course), "UTF-8")
                    navController.navigate("${AppDestination.CourseDetails.route}/$courseJson")
                }
            )
        }
    }
}