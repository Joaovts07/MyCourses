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
import com.example.mycourses.model.Course
import com.example.mycourses.model.getCourse
import com.example.mycourses.model.serializeCourse
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
    title: String = "Cursos Em Destaques",
    onNavigateToCheckout: () -> Unit = {},
    onNavigateToDetails: (String) -> Unit = {}
) {
    val courses = remember { mutableStateListOf<Course>() }
    val coroutineScope = rememberCoroutineScope()
    LaunchedEffect(Unit) {
        coroutineScope.launch {
            try {
                val documents = Firebase.firestore.collection("courses").get().await()
                courses.addAll(documents.map { document ->
                    getCourse(document)
                })
            } catch (e: Exception) {
                Log.e("cath", e.message.toString())
            }
        }
    }

    Column(
        modifier
            .fillMaxSize()
    ) {
        Surface {
            Text(
                text = title,
                Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                fontFamily = caveatFont,
                fontSize = 32.sp,
                textAlign = TextAlign.Center
            )

        }
        Spacer(Modifier.height(4.dp))
        Text(
            text = "Porque a melhor maneira de aprender é ensinando",
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            fontSize = 18.sp,
            textAlign = TextAlign.Center
        )
        LazyColumn(
            modifier
                .fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(courses) { course ->
                HighlighCourseCard(
                    course = course,
                    Modifier.clickable {
                        val courseJson = serializeCourse(course)
                        onNavigateToDetails(courseJson)
                    },
                    onOrderClick = onNavigateToCheckout
                )
            }     
        }
    }
}

@Preview
@Composable
fun HighlightsListScreenPreview() {
    MyCoursesTheme {
        Surface {
            CoursesListScreen(
                title = "Cursos Em Destaques Porque a melhor maneira de aprender é ensinando"
            )
        }
    }
}