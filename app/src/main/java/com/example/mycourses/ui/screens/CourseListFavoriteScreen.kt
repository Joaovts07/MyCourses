package com.example.mycourses.ui.screens

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
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
import coil.compose.AsyncImage
import com.example.mycourses.R
import com.example.mycourses.model.entities.Course
import com.example.mycourses.model.entities.getCourse
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch

@Composable
fun CourseFavoriteScreen(
    modifier: Modifier = Modifier,
    onNavigateToCheckout: () -> Unit = {}
) {
    val courseList = remember { mutableStateListOf<Course?>() }
    val coroutineScope = rememberCoroutineScope()
    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
    val userDocRef = Firebase.firestore.collection("users").document(userId)

    Column(
        modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        LaunchedEffect(Unit) {
            coroutineScope.launch {
                try {
                    userDocRef.get().addOnSuccessListener { document ->
                        if (document != null) {
                            val favoriteCourses = document.data?.get("favoriteCourses") as? Map<*, *>
                            if (favoriteCourses != null) {
                                val favoriteCourseIds = favoriteCourses.keys.toList()
                                if (favoriteCourseIds.isNotEmpty()) {
                                    Firebase.firestore.collection("courses")
                                        .whereIn("id", favoriteCourseIds)
                                        .get()
                                        .addOnSuccessListener { querySnapshot ->
                                            for (favoriteCourse in querySnapshot) {
                                                courseList.add(getCourse(favoriteCourse))
                                            }
                                        }
                                }

                            }
                        }
                    }

                } catch (e: Exception) {
                    Log.e("cath", e.message.toString())
                }
            }
        }
        for (course in courseList) {
            course?.image?.let { 
                AsyncImage(
                    model = course.image,
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
                            Text(it.rate.toString())
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.Filled.Star,
                                contentDescription = "Avaliação",
                                tint = Color.Yellow
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