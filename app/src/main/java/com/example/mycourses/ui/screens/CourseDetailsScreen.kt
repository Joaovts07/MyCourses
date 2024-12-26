package com.example.mycourses.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.mycourses.R
import com.example.mycourses.model.Course
import com.example.mycourses.sampledata.sampleCourseWithImage
import com.example.mycourses.ui.theme.MyCoursesTheme

@Composable
fun CourseDetailsScreen(
    course: Course,
    modifier: Modifier = Modifier,
    onNavigateToCheckout: () -> Unit = {}
) {
    Column(
        modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        course.image?.let {
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
            Text(course.name, fontSize = 24.sp)
            Text(course.price.toPlainString(), fontSize = 18.sp)
            Text(course.description)
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

@Preview
@Composable
fun ProductDetailsScreenPreview() {
    MyCoursesTheme {
        Surface {
            CourseDetailsScreen(
                course = sampleCourseWithImage,
            )
        }
    }
}