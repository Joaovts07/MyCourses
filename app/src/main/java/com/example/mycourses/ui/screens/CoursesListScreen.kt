package com.example.mycourses.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.mycourses.model.states.CourseUiState
import com.example.mycourses.ui.components.ErrorScreen
import com.example.mycourses.ui.components.HighlighCourseCard
import com.example.mycourses.ui.components.LoadingScreen
import com.example.mycourses.ui.theme.caveatFont
import com.example.mycourses.viewmodels.CoursesListViewModel

@Composable
fun CoursesListScreen(
    modifier: Modifier = Modifier,
    onNavigateToDetails: (String) -> Unit = {},
    viewModel: CoursesListViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    Column(
        modifier.fillMaxSize().padding(top = 14.dp)
    ) {
        Surface {
            Text(
                text = "Cursos Em Destaques",
                modifier = Modifier
                    .fillMaxWidth(),
                fontFamily = caveatFont,
                fontSize = 32.sp,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.primary
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Porque a melhor maneira de aprender é ensinando",
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            fontSize = 18.sp,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(8.dp))

        when(uiState) {
            is CourseUiState.Loading -> {
                LoadingScreen()
            }
            is CourseUiState.Error -> ErrorScreen((uiState as CourseUiState.Error).message)

            is CourseUiState.Success -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items((uiState as CourseUiState.Success).courses) { course ->
                        HighlighCourseCard(
                            course = course,
                            modifier = Modifier.clickable { onNavigateToDetails(course.id) },
                        )
                    }
                }
            }
        }
    }
}