package com.example.mycourses.ui.screens.createCourse

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import com.example.mycourses.ui.components.DialogHandler
import com.example.mycourses.ui.components.LoadingButton
import com.example.mycourses.viewmodels.CourseCreationViewModel

@Composable
fun CourseReviewScreen(
    viewModel: CourseCreationViewModel,
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val dialogState by viewModel.dialogState.collectAsState()
    val imageUri by viewModel.imageUri

    Column(modifier = Modifier.fillMaxSize().padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Revisão do Curso", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(16.dp))

        Text("Título: ${uiState.name}")
        Text("Descrição: ${uiState.description}")
        Text("Categoria: ${uiState.category}")
        val image = if (uiState.image != null) { uiState.image!!.toUri() } else { imageUri }

        ImageSelector(image)

        Spacer(modifier = Modifier.height(24.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            val text = if (uiState.instructorId.isEmpty()) "Cadastrar Curso" else "Atualizar Curso"
            LoadingButton(text = text) {
                if (uiState.instructorId.isEmpty()) {
                    viewModel.submitCourse()
                } else {
                    viewModel.updateCourse()
                }
            }
        }
    }
    DialogHandler(dialogState, onDismiss = { viewModel.dismissDialog(onBack) })

}