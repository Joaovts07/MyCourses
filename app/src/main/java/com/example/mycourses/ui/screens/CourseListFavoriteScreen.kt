package com.example.mycourses.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.mycourses.model.entities.Course
import com.example.mycourses.ui.components.HighlighCourseCard
import com.example.mycourses.viewmodels.CoursesListViewModel

@Composable
fun CourseFavoriteScreen(
    modifier: Modifier = Modifier,
    onNavigateToCheckout: () -> Unit = {},
    onNavigateToDetails: (Course) -> Unit = {},
    viewModel: CoursesListViewModel
) {
    val favoriteCourses = viewModel.favoriteCourses
    val isLoading = viewModel.isLoading
    val errorMessage = viewModel.errorMessage

    LaunchedEffect(key1 = true) {
        viewModel.loadFavoriteCourses()
    }

    Column(
        modifier = modifier
            .fillMaxSize()
    ) {
        Text(
            text = "Cursos Favoritos",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(16.dp)
        )

        when {
            isLoading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            errorMessage != null -> {
                Text(
                    text = "Erro ao carregar favoritos: $errorMessage",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(16.dp)
                )
            }
            favoriteCourses.isEmpty() -> {
                Text(
                    text = "Você não tem cursos favoritos.",
                    modifier = Modifier.padding(16.dp)
                )
            }
            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ){
                    items(favoriteCourses.size){ course ->
                        HighlighCourseCard(
                            course = favoriteCourses[course],
                            modifier = Modifier.clickable { onNavigateToDetails(favoriteCourses[course]) },
                            onOrderClick = onNavigateToCheckout
                        )
                    }
                }
            }
        }
    }
}