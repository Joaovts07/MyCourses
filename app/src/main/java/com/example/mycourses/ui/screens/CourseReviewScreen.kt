package com.example.mycourses.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import com.example.mycourses.viewmodels.CourseCreationViewModel

@Composable
fun CourseReviewScreen(
    viewModel: CourseCreationViewModel = hiltViewModel(),
    onSubmit: () -> Unit,
    onBack: () -> Unit
) {
    val uiState = viewModel.uiState

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Revisão do Curso", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(16.dp))

        Text("Título: ${uiState.title}")
        Text("Categoria: ${uiState.category}")
        uiState.imageUri?.let { imageUri ->
            Image(painter = rememberAsyncImagePainter(imageUri), contentDescription = null, modifier = Modifier.size(200.dp))
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Button(onClick = onBack) { Text("Voltar") }
            Button(onClick = onSubmit) { Text("Criar Curso") }
        }
    }
}