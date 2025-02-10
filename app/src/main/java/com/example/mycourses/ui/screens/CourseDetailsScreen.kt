package com.example.mycourses.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.mycourses.R
import com.example.mycourses.model.entities.Course
import com.example.mycourses.model.entities.Subscription
import com.example.mycourses.model.states.CourseUiState
import com.example.mycourses.model.states.DialogState
import com.example.mycourses.ui.components.RatingBar
import com.example.mycourses.viewmodels.CourseDetailsViewModel

@Composable
fun CourseDetailsScreen(
    course: Course,
    viewModel: CourseDetailsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val ratingUpdated by viewModel.ratingUpdated.collectAsState()
    val dialogState by viewModel.dialogState.collectAsState()

    viewModel.initialize(course)

    when (uiState) {
        is CourseUiState.Loading -> LoadingScreen()
        is CourseUiState.Success -> {
            val course = (uiState as CourseUiState.Success).course
            val isFavorite = (uiState as CourseUiState.Success).isFavorite
            val subscription = (uiState as CourseUiState.Success).subscription
            CourseContent(
                course = course,
                isFavorite = isFavorite,
                subscription = subscription,
                onToggleFavorite = { viewModel.toggleFavorite(course.id) },
                onEnrollClick = { viewModel.subscribeCourse(course.id) },
                onRatingUpdate = { newRating ->
                    subscription?.let { viewModel.updateRating(it.id, newRating) }
                },
                ratingUpdated = ratingUpdated,
                onResetRating = { viewModel.resetRatingUpdated() }
            )
        }
        is CourseUiState.Error -> {
            val message = (uiState as CourseUiState.Error).message
            //ErrorScreen(message)
        }

    }

    DialogHandler(
        dialogState = dialogState,
        onDismiss = { viewModel.dismissDialog() }
    )
}
@Composable
fun CourseContent(
    course: Course,
    isFavorite: Boolean,
    subscription: Subscription?,
    onToggleFavorite: () -> Unit,
    onEnrollClick: () -> Unit,
    onRatingUpdate: (Float) -> Unit,
    ratingUpdated: Boolean,
    onResetRating: () -> Unit
) {
    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        AsyncImage(
            model = course.image,
            contentDescription = null,
            modifier = Modifier
                .height(200.dp)
                .fillMaxWidth(),
            placeholder = painterResource(id = R.drawable.placeholder),
            contentScale = ContentScale.Crop
        )

        Column(Modifier.padding(16.dp)) {
            Text(course.name, fontSize = 24.sp)
            Text(course.description)

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row {
                    Text(course.rating)
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.Filled.Star,
                        contentDescription = "Avaliação",
                        tint = Color.Yellow
                    )
                }

                IconButton(onClick = onToggleFavorite) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                        contentDescription = "Favoritar curso",
                        tint = if (isFavorite) Color.Red else Color.LightGray
                    )
                }
            }

            if (subscription == null) {
                EnrollButton(onEnrollClick)
            } else {
                RatingBar(rating = subscription.rating.toFloat(), onRatingChange = onRatingUpdate)
                if (ratingUpdated) onResetRating()
            }
        }
    }
}

@Composable
fun EnrollButton(onEnrollClick: () -> Unit) {
    Button(
        onClick = onEnrollClick,
        modifier = Modifier.fillMaxWidth(),
        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
    ) {
        Text(text = "Cadastrar")
    }
}

@Composable
fun DialogHandler(dialogState: DialogState, onDismiss: () -> Unit) {
    when (dialogState) {
        is DialogState.Success -> {
            AlertDialog(
                onDismissRequest = onDismiss,
                confirmButton = {
                    Button(onClick = onDismiss) { Text("OK") }
                },
                title = { Text("Sucesso") },
                text = { Text(dialogState.message) }
            )
        }
        is DialogState.Error -> {
            AlertDialog(
                onDismissRequest = onDismiss,
                confirmButton = {
                    Button(onClick = onDismiss) { Text("OK") }
                },
                title = { Text("Erro") },
                text = { Text(dialogState.message) }
            )
        }
        DialogState.None -> {}
    }
}

@Composable
fun LoadingScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}