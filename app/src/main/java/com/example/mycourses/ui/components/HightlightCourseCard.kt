package com.example.mycourses.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import com.example.mycourses.R
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.mycourses.sampledata.sampleCourseWithImage
import com.example.mycourses.sampledata.sampleCourseWithoutImage
import coil.compose.AsyncImage
import com.example.mycourses.model.entities.Course
import com.example.mycourses.ui.theme.MyCoursesTheme


@Composable
fun HighlighCourseCard(
    course: Course?,
    modifier: Modifier = Modifier,
    onOrderClick: () -> Unit = {}
) {
    Card(
        modifier
            .clip(RoundedCornerShape(12.dp))
            .fillMaxWidth()
    ) {
        Column(Modifier.fillMaxWidth()) {
            course?.image?.let { image ->
                AsyncImage(
                    image,
                    contentDescription = null,
                    Modifier
                        .fillMaxWidth()
                        .height(116.dp),
                    placeholder = painterResource(id = R.drawable.placeholder),
                    contentScale = ContentScale.Crop,
                )
            }
            Column(
                Modifier.padding(
                    horizontal = 16.dp,
                    vertical = 8.dp
                )
            ) {
                course?.let {
                    Text(text = course.name)
                    Text(text = course.price.toString())
                    Spacer(Modifier.height(16.dp))
                    Text(
                        text = course.description,
                        maxLines = 5,
                        overflow = TextOverflow.Ellipsis
                    )
                }

            }
            Spacer(Modifier.height(18.dp))

        }
    }
}

@Preview
@Composable
private fun HighlightProductPreview() {
    MyCoursesTheme {
        HighlighCourseCard(
            course = sampleCourseWithoutImage
        )
    }
}

@Preview
@Composable
private fun HighlightProductCardWithImagePreview() {
    MyCoursesTheme {
        HighlighCourseCard(
            course = sampleCourseWithImage
        )
    }
}
