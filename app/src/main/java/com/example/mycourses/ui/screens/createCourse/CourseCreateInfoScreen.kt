package com.example.mycourses.ui.screens.createCourse

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.mycourses.viewmodels.CourseCreationViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CourseInfoScreen(
    courseId: String? = null,
    courseCreationViewModel: CourseCreationViewModel,
    onNext: () -> Unit,
    onBack: () -> Unit
) {
    LaunchedEffect(courseId) {
        if (courseId != null) {
            courseCreationViewModel.loadCourse(courseId)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Informações do Curso") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Blue,
                    titleContentColor = Color.White
                ),
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Voltar",
                            tint = Color.White
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        CourseInfoContent(
            courseCreationViewModel = courseCreationViewModel,
            onNext = onNext,
            modifier = Modifier.padding(innerPadding)
        )
    }
}

@Composable
fun CourseInfoContent(
    courseCreationViewModel: CourseCreationViewModel,
    onNext: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by courseCreationViewModel.uiState.collectAsState()
    Column(modifier = modifier.fillMaxSize().padding(start = 16.dp, end = 16.dp, top = 16.dp)) {
        TextField(
            value = uiState.name,
            onValueChange = { courseCreationViewModel.updateTitle(it) },
            label = { Text("Título do Curso") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = uiState.description,
            onValueChange = { courseCreationViewModel.updateDescription(it) },
            label = { Text("Descrição") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = uiState.category,
            onValueChange = { courseCreationViewModel.updateCategory(it) },
            label = { Text("Categoria") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                courseCreationViewModel.updateCourseInfo(uiState)
                onNext()
            },
            modifier = Modifier.align(Alignment.End)
        ) {
            Text("Próximo")
        }
    }
}