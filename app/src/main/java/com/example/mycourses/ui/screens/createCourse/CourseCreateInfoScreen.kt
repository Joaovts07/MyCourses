package com.example.mycourses.ui.screens.createCourse

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.mycourses.viewmodels.CourseCreationViewModel

@Composable
fun CourseInfoScreen(
    courseCreationViewModel: CourseCreationViewModel = hiltViewModel(),
    onNext: () -> Unit
) {
    var title by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize().padding(start = 16.dp, end = 16.dp, top = 40.dp)) {
        TextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Título do Curso") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = category,
            onValueChange = { category = it },
            label = { Text("Categoria") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                courseCreationViewModel.updateCourseInfo(title, category)
                onNext()
            },
            modifier = Modifier.align(Alignment.End)
        ) {
            Text("Próximo")
        }
    }
}