package com.example.mycourses.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.mycourses.model.entities.Course
import com.example.mycourses.model.states.CourseUiState
import com.example.mycourses.ui.components.ErrorScreen
import com.example.mycourses.ui.components.HighlighCourseCard
import com.example.mycourses.ui.components.LoadingScreen
import com.example.mycourses.viewmodels.CoursesListViewModel

@Composable
fun CourseFavoriteScreen(
    modifier: Modifier = Modifier,
    onNavigateToDetails: (Course) -> Unit = {},
    viewModel: CoursesListViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(key1 = true) {
        viewModel.loadFavoriteCourses()
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(top = 32.dp)
    ) {
        Text(
            text = "Cursos Favoritos",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(16.dp).align(Alignment.CenterHorizontally)
        )

        when(uiState) {
            is CourseUiState.Loading -> { LoadingScreen() }

            is CourseUiState.Error -> { ErrorScreen((uiState as CourseUiState.Error).message) }

            is CourseUiState.Success -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ){
                    val favoriteCourses = (uiState as CourseUiState.Success).courses
                    items(favoriteCourses.size){ course ->
                        HighlighCourseCard(
                            course = favoriteCourses[course],
                            modifier = Modifier.clickable { onNavigateToDetails(favoriteCourses[course]) },
                        )
                    }
                }
            }
        }
    }
}