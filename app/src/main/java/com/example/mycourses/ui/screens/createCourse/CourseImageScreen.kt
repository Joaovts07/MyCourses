package com.example.mycourses.ui.screens.createCourse

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.mycourses.viewmodels.CourseCreationViewModel

@Composable
fun CourseImageScreen(
    viewModel: CourseCreationViewModel,
    onNext: () -> Unit,
    onBack: () -> Unit
) {
    val imageUri by viewModel.imageUri
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        viewModel.updateCourseImage(uri)
    }
    val uiState by viewModel.uiState.collectAsState()

    Column(modifier = Modifier.fillMaxSize().padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        val image = if (uiState.image != null) { uiState.image } else { imageUri }

        Image(painter = rememberAsyncImagePainter(image), contentDescription = null, modifier = Modifier.size(200.dp))



        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            launcher.launch("image/*")
        }) {
            Text("Selecionar Imagem")
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Button(onClick = onBack) { Text("Voltar") }
            Button(onClick = {
                imageUri?.let {
                    viewModel.updateCourseImage(imageUri)
                }
                onNext()
            }) { Text("Próximo") }
        }
    }
}