package com.example.mycourses.ui.screens.createCourse

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.mycourses.viewmodels.CourseCreationViewModel

@Composable
fun CourseInfoScreen(
    courseId: String? = null,
    courseCreationViewModel: CourseCreationViewModel,
    onNext: () -> Unit,
) {
    LaunchedEffect(courseId) {
        if (courseId != null) {
            courseCreationViewModel.loadCourse(courseId)
        }
    }

    CourseInfoContent(
        courseCreationViewModel = courseCreationViewModel,
        onNext = onNext,
    )

}

@Composable
fun CourseInfoContent(
    courseCreationViewModel: CourseCreationViewModel,
    onNext: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by courseCreationViewModel.uiState.collectAsState()
    val imageUri by courseCreationViewModel.imageUri
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        courseCreationViewModel.updateCourseImage(uri)
    }
    Column(modifier = modifier.fillMaxSize().padding(start = 16.dp, end = 16.dp, top = 16.dp)) {
        val image = if (uiState.image != null) { uiState.image } else { imageUri }
        Image(painter = rememberAsyncImagePainter(image), contentDescription = null, modifier = Modifier.size(200.dp))
        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            launcher.launch("image/*")
        }) {
            Text("Selecionar Imagem")
        }

        Spacer(modifier = Modifier.height(24.dp))

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