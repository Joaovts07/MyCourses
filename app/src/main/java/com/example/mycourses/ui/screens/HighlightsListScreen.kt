package com.example.mycourses.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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


@Composable
fun HighlightsListScreen(
    modifier: Modifier = Modifier,
    title: String = "Cursos Em Destaques",
    products: List<Course> = emptyList(),
    onNavigateToCheckout: () -> Unit = {},
    onNavigateToDetails: (Course) -> Unit = {}
) {
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
            items(products) { p ->
                HighlighCourseCard(
                    product = p,
                    Modifier.clickable {
                        onNavigateToDetails(p)
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
                products = sampleCourses,
                title = "Cursos Em Destaques"
            )
        }
    }
}