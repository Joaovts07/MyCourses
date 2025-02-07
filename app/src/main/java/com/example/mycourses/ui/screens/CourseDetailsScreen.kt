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
import com.example.mycourses.ui.components.RatingBar
import com.example.mycourses.viewmodels.CourseDetailsViewModel

@Composable
fun CourseDetailsScreen(
    course: Course?,
    modifier: Modifier = Modifier,
    onNavigateToCheckout: () -> Unit = {},
    viewModel: CourseDetailsViewModel = hiltViewModel()
) {
    val isFavorite = viewModel.isFavorite

    LaunchedEffect(course) {
        course?.let { viewModel.initialize(it) }
    }

    Column(
        modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        if (course != null) {
            AsyncImage(
                model = course.image,
                contentDescription = null,
                modifier = Modifier
                    .height(200.dp)
                    .fillMaxWidth(),
                placeholder = painterResource(id = R.drawable.placeholder),
                contentScale = ContentScale.Crop
            )
            Column(
                Modifier
                    .padding(16.dp)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(course.name, fontSize = 24.sp)
                Text(course.description)
                RatingBar(rating = 3f) { newRating ->
                    viewModel.updateRating(course.id, newRating)
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    //Text(course.instructor, fontSize = 18.sp)
                    Row {

                        Text(course.rating)
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Filled.Star,
                            contentDescription = "Avaliação",
                            tint = Color.Yellow
                        )
                    }

                    IconButton(onClick = { viewModel.toggleFavorite(course.id) }) {
                        Icon(
                            imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                            contentDescription = "Favoritar curso",
                            tint = if (isFavorite) Color.Red else Color.LightGray
                        )
                    }
                }

                Button(
                    onClick = { onNavigateToCheckout() },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text(text = "Cadastrar")
                }
            }
        }
    }
}