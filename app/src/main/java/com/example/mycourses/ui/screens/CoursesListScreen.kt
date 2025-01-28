package com.example.mycourses.ui.screens

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mycourses.model.entities.Course
import com.example.mycourses.model.entities.getCourse
import com.example.mycourses.ui.components.HighlighCourseCard
import com.example.mycourses.ui.theme.MyCoursesTheme
import com.example.mycourses.ui.theme.caveatFont
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await


@Composable
fun CoursesListScreen(
    modifier: Modifier = Modifier,
    onNavigateToCheckout: () -> Unit = {},
    onNavigateToDetails: (Course) -> Unit = {},
    viewModel: CoursesListViewModel
) {
    val courses = viewModel.courses
    val isLoading = viewModel.isLoading
    val errorMessage = viewModel.errorMessage
    Log.d("CoursesListScreen", "Composable chamado")
    Column(
        modifier.fillMaxSize()
    ) {
        Surface {
            Text(
                text = "Cursos Em Destaques",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
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

        when {
            isLoading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            errorMessage != null -> {
                Text(
                    text = "Erro ao carregar cursos: $errorMessage",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(16.dp)
                )
            }
            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(courses) { course ->
                        HighlighCourseCard(
                            course = course,
                            modifier = Modifier.clickable { onNavigateToDetails(course) },
                            onOrderClick = onNavigateToCheckout
                        )
                    }
                }
            }
        }
    }
}