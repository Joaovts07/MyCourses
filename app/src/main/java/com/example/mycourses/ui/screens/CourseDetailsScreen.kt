package com.example.mycourses.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.mycourses.model.entities.Comment
import com.example.mycourses.model.entities.Course
import com.example.mycourses.model.entities.Subscription
import com.example.mycourses.model.entities.User
import com.example.mycourses.model.states.CourseDetailsUiState
import com.example.mycourses.ui.components.DialogHandler
import com.example.mycourses.ui.components.ErrorScreen
import com.example.mycourses.ui.components.LoadingButton
import com.example.mycourses.ui.components.LoadingScreen
import com.example.mycourses.ui.components.RatingBar
import com.example.mycourses.ui.components.ShareCourseButton
import com.example.mycourses.viewmodels.CourseDetailsViewModel

@Composable
fun CourseDetailsScreen(
    courseId: String,
    viewModel: CourseDetailsViewModel = hiltViewModel(),
    onEditCourse: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val ratingUpdated by viewModel.ratingUpdated.collectAsState()
    val dialogState by viewModel.dialogState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.initialize(courseId)
    }

    when (uiState) {
        is CourseDetailsUiState.Loading -> LoadingScreen()
        is CourseDetailsUiState.Success -> {
            val state = uiState as CourseDetailsUiState.Success
            val userId = state.subscription?.userId ?: ""
            CourseContent(
                course = state.course,
                isFavorite = state.isFavorite,
                subscription = state.subscription,
                commentsWithUsers = state.commentsWithUsers,
                onToggleFavorite = { viewModel.toggleFavorite(courseId) },
                onEnrollClick = { viewModel.subscribeCourse(courseId) },
                onRatingUpdate = { rating -> state.subscription?.let { viewModel.updateRating(it.id, rating) } },
                ratingUpdated = ratingUpdated,
                onResetRating = { viewModel.resetRatingUpdated() },
                onAddComment = { comment -> viewModel.addComment(userId,state.course.id, comment) },
                isMyCourse = state.isMyCourse,
                onEditClick = { onEditCourse()  }
            )
        }
        is CourseDetailsUiState.Error -> ErrorScreen((uiState as CourseDetailsUiState.Error).message,)
    }

    DialogHandler(dialogState, onDismiss = { viewModel.dismissDialog() })
}

@Composable
fun CourseContent(
    course: Course,
    isFavorite: Boolean,
    subscription: Subscription?,
    commentsWithUsers: List<Pair<Comment, User?>>,
    onToggleFavorite: () -> Unit,
    onEnrollClick: () -> Unit,
    onRatingUpdate: (Float) -> Unit,
    ratingUpdated: Boolean,
    onResetRating: () -> Unit,
    onAddComment: (String) -> Unit,
    isMyCourse: Boolean = false,
    onEditClick: () -> Unit = {}
) {
    var showCommentDialog by remember { mutableStateOf(false) }
    var commentText by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize()) {
        AsyncImage(
            model = course.image,
            contentDescription = null,
            modifier = Modifier
                .fillMaxHeight(0.5f)
                .fillMaxWidth(),
            contentScale = ContentScale.Crop
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp)
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = course.name,
                        fontSize = 24.sp,
                        modifier = Modifier
                            .weight(1f)
                            .padding(bottom = 8.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp)) 

                    IconButton(onClick = onToggleFavorite) {
                        Icon(
                            imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                            contentDescription = "Favoritar",
                            tint = if (isFavorite) Color.Red else Color.LightGray
                        )
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    ShareCourseButton(
                        courseName = course.name,
                        courseDescription = course.description,
                        courseLink = "https://seuapp.com/curso/${course.id}",
                        courseImageUrl = course.image
                    )
                }

                Text(course.description)

                if (subscription != null) {
                    RatingBar(rating = subscription.rating.toFloat(), onRatingChange = onRatingUpdate)
                    if (ratingUpdated) onResetRating()
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(course.rate.toString())
                }

                if (isMyCourse) {
                    LoadingButton(
                        onClick = onEditClick,
                        text   = "Editar Curso"
                    )
                }
                if (subscription == null && !isMyCourse) {
                    LoadingButton(
                        onClick = onEnrollClick,
                        text = "Inscrever-se"
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
                Text("Comentários", style = MaterialTheme.typography.titleMedium)

                LazyColumn(modifier = Modifier.fillParentMaxHeight(0.5f)) {
                    items(commentsWithUsers) { (comment, user) ->
                        CommentItem(comment, user)
                    }
                }
            }
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.BottomEnd
        ) {
            FloatingActionButton(
                modifier = Modifier.size(48.dp),
                onClick = { showCommentDialog = true },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Adicionar Comentário",
                    tint = Color.White
                )
            }
        }
    }

    if (showCommentDialog) {
        AlertDialog(
            onDismissRequest = { showCommentDialog = false },
            title = { Text("Novo Comentário") },
            text = {
                OutlinedTextField(
                    value = commentText,
                    onValueChange = { commentText = it },
                    label = { Text("Digite seu comentário") },
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(onClick = {
                    if (commentText.isNotBlank()) {
                        onAddComment(commentText)
                        commentText = ""
                        showCommentDialog = false
                    }
                }) { Text("Salvar") }
            },
            dismissButton = {
                TextButton(onClick = { showCommentDialog = false }) { Text("Cancelar") }
            }
        )
    }
}

@Composable
fun CommentItem(comment: Comment, user: User?) {
    Row(modifier = Modifier.padding(8.dp)) {
        if (user?.profilePictureUrl?.isNotEmpty() == true) {
            AsyncImage(
                model = user.profilePictureUrl,
                contentDescription = "Foto do usuário",
                modifier = Modifier.size(40.dp).clip(CircleShape)
            )
        } else {
            Box(
                modifier = Modifier.size(40.dp).clip(CircleShape).background(Color.Blue),
                contentAlignment = Alignment.Center
            ) {
                Text(text = user?.name?.firstOrNull()?.toString() ?: "?", color = Color.White)
            }
        }
        Column(modifier = Modifier.padding(start = 8.dp)) {
            Text(user?.name ?: "Usuário desconhecido", fontWeight = FontWeight.Bold)
            Text(comment.text)
        }
    }
}
