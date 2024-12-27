package com.example.mycourses.ui.screens

import android.util.Log
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.mycourses.R
import com.example.mycourses.model.Course
import com.example.mycourses.model.getCourse
import com.example.mycourses.ui.theme.MyCoursesTheme
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@Composable
fun CourseDetailsScreen(
    courseId : String,
    modifier: Modifier = Modifier,
    onNavigateToCheckout: () -> Unit = {}
) {
    var isFavorite by remember { mutableStateOf(false) }
    var course by remember { mutableStateOf<Course?>(null) }
    val coroutineScope = rememberCoroutineScope()

    val db = Firebase.firestore
    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
    val userDocRef = db.collection("users").document(userId)

    fun favoriteCourse(courseId: String) {
        if (isFavorite) {
            userDocRef.update("favoriteCourses.$courseId", true)
                .addOnSuccessListener {
                    isFavorite = true
                }
                .addOnFailureListener { e ->
                    isFavorite = false
                }
        } else {
            val updates = hashMapOf<String, Any>(
                "favoriteCourses.$courseId" to FieldValue.delete()
            )
            userDocRef.update(updates)
                .addOnSuccessListener {
                    isFavorite = false
                }
                .addOnFailureListener { e ->
                    isFavorite = true
                }
        }

    }
    Column(
        modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        LaunchedEffect(courseId) {
            coroutineScope.launch {
                try {
                    val document = Firebase.firestore.collection("courses")
                        .document(courseId)
                        .get().await()

                    if (document.exists()) {
                        course = getCourse(document)
                    } else {
                        Log.e("else","curso nao encontrado")
                    }
                } catch (e: Exception) {
                    Log.e("cath", e.message.toString())
                }
            }
        }
        if(course != null ) {
            course?.image?.let {
                AsyncImage(
                    model = course?.image,
                    contentDescription = null,
                    modifier = Modifier
                        .height(200.dp)
                        .fillMaxWidth(),
                    placeholder = painterResource(id = R.drawable.placeholder),
                    contentScale = ContentScale.Crop
                )
            }
            Column(
                Modifier
                    .padding(16.dp)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                course?.let {
                    Text(it.name, fontSize = 24.sp)
                    Text(it.description)
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(it.price, fontSize = 18.sp)
                        Row {
                            Text(it.rate)
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.Filled.Star,
                                contentDescription = "Avaliação",
                                tint = Color.Yellow
                            )
                        }

                        IconButton(onClick = { favoriteCourse(courseId) }) {
                            Icon(
                                imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                                contentDescription = "Favoritar curso",
                                tint = if (isFavorite) Color.Red else Color.LightGray
                            )
                        }

                    }
                }


                Button(
                    onClick = { onNavigateToCheckout() },
                    Modifier
                        .fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text(text = "Cadastrar")
                }

            }
        }

    }

}

@Preview
@Composable
fun CourseDetailsScreenPreview() {
    MyCoursesTheme {
        Surface {
            CourseDetailsScreen(
                courseId = "1",
            )
        }
    }
}