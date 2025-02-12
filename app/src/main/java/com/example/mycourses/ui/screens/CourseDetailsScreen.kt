package com.example.mycourses.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
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
import com.example.mycourses.model.entities.Comment
import com.example.mycourses.model.entities.Course
import com.example.mycourses.model.entities.Subscription
import com.example.mycourses.model.states.CourseDetailsUiState
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
        is CourseDetailsUiState.Loading -> LoadingScreen()
        is CourseDetailsUiState.Success -> {
            val course = (uiState as CourseDetailsUiState.Success).course
            val isFavorite = (uiState as CourseDetailsUiState.Success).isFavorite
            val subscription = (uiState as CourseDetailsUiState.Success).subscription
            val comments = (uiState as CourseDetailsUiState.Success).comments
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
                onResetRating = { viewModel.resetRatingUpdated() },
                comments = comments,
                viewModel
            )
        }
        is CourseDetailsUiState.Error -> {
            val message = (uiState as CourseDetailsUiState.Error).message
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
    onResetRating: () -> Unit,
    comments: List<Comment> = emptyList(),
    viewModel: CourseDetailsViewModel
) {
    var showCommentDialog by remember { mutableStateOf(false) }
    var commentText by remember { mutableStateOf("") }
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

            if(subscription != null) {
                RatingBar(rating = subscription.rating.toFloat(), onRatingChange = onRatingUpdate)
                if (ratingUpdated) onResetRating()
            }

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

            Spacer(modifier = Modifier.height(16.dp))
            if (comments.isNotEmpty()) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Comentários", style = MaterialTheme.typography.titleMedium)

                    FloatingActionButton(
                        modifier = Modifier.size(22.dp),
                        onClick = { showCommentDialog = true },
                        containerColor = MaterialTheme.colorScheme.primary
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Adicionar Comentário",
                        )
                    }
                }

                comments.forEach {
                        comment ->
                    CommentItem(comment)

                }




                /** Dialog para Adicionar Comentário */
                if (showCommentDialog) {
                    AlertDialog(
                        onDismissRequest = { showCommentDialog = false },
                        title = { Text("Novo Comentário") },
                        text = {
                            Column {
                                OutlinedTextField(
                                    value = commentText,
                                    onValueChange = { commentText = it },
                                    label = { Text("Digite seu comentário") },
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        },
                        confirmButton = {
                            Button(onClick = {
                                if (commentText.isNotBlank()) {
                                    viewModel.addComment(course.id, commentText)
                                    commentText = ""
                                    showCommentDialog = false
                                }
                            }) {
                                Text("Salvar")
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = { showCommentDialog = false }) {
                                Text("Cancelar")
                            }
                        }
                    )
                }
            }

            if (subscription == null) {
                EnrollButton(onEnrollClick)
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

@Composable
fun CommentItem(comment: Comment) {
    Column(modifier = Modifier.padding(8.dp)) {
        Text("nome")
        Text(comment.text)
        //Text(comment.timestamp.toString(), style = MaterialTheme.typography.bodySmall)
    }
}