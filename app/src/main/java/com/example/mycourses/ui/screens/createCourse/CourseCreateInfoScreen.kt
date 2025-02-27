package com.example.mycourses.ui.screens.createCourse

import android.net.Uri
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import coil.compose.AsyncImage
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
        val image = if (uiState.image != null) { uiState.image!!.toUri() } else { imageUri }
        ImageSelector(image,launcher)

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

@Composable
fun ImageSelector(
    imageUri: Uri?,
    launcher: ManagedActivityResultLauncher<String, Uri?>
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color.Gray.copy(alpha = 0.2f))
            .clickable { launcher.launch("image/*") }
    ) {
        if (imageUri != null) {
            AsyncImage(
                model = imageUri,
                contentDescription = "Imagem do curso",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            IconButton(
                onClick = { launcher.launch("image/*") } ,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .clip(CircleShape)
                    .size(40.dp)
                    .background(Color.Gray, CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Filled.Edit,
                    contentDescription = "Editar foto",
                    tint = Color.White
                )
            }
        } else {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.AddAPhoto,
                    contentDescription = "Selecionar imagem",
                    modifier = Modifier.size(64.dp),
                    tint = Color.DarkGray
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Adicionar Imagem",
                    fontSize = 14.sp,
                    color = Color.DarkGray
                )
            }
        }
    }
}
