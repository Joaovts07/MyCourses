package com.example.mycourses.ui.screens

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mycourses.sampledata.sampleCourses
import com.example.mycourses.model.Course
import com.example.mycourses.ui.components.HighlighCourseCard
import com.example.mycourses.ui.theme.MyCoursesTheme
import com.example.mycourses.ui.theme.caveatFont
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.math.BigDecimal


@Composable
fun HighlightsListScreen(
    modifier: Modifier = Modifier,
    title: String = "Cursos Em Destaques",
    onNavigateToCheckout: () -> Unit = {},
    onNavigateToDetails: (String) -> Unit = {}
) {
    val courses = remember { mutableStateListOf<Course>() }
    val coroutineScope = rememberCoroutineScope()
    LaunchedEffect(Unit) {
        coroutineScope.launch {
            try {
                val documents = Firebase.firestore.collection("cursos").get().await()
                courses.addAll(documents.map { document ->
                    Course(
                        id = document.id,
                        name = document["nome"] as String,
                        description = document["descricao_curso"] as String,
                        image = document["imagem"] as String?,
                        price = BigDecimal(document["preco"].toString()),
                    )
                })
            } catch (e: Exception) {
                Log.e("cath", e.message.toString())
            }
        }
    }

    Column(
        modifier
            .fillMaxSize()
    ) {
        Surface {
            Text(
                text = title,
                Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                fontFamily = caveatFont,
                fontSize = 32.sp,
                textAlign = TextAlign.Center
            )
        }
        LazyColumn(
            modifier
                .fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(courses) { p ->
                HighlighCourseCard(
                    course = p,
                    Modifier.clickable {
                        onNavigateToDetails(p.id)
                    },
                    onOrderClick = onNavigateToCheckout
                )
            }
        }
    }
}

@Preview
@Composable
fun HighlightsListScreenPreview() {
    MyCoursesTheme {
        Surface {
            HighlightsListScreen(
                title = "Cursos Em Destaques"
            )
        }
    }
}