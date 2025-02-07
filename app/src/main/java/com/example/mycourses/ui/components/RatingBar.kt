package com.example.mycourses.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun RatingBar(
    rating: Float,
    onRatingChange: (Float) -> Unit
) {
    var currentRating by remember { mutableFloatStateOf(rating) }

    Row {
        for (i in 1..5) {
            Icon(
                imageVector = if (i <= currentRating) Icons.Filled.Star else Icons.Outlined.StarBorder,
                contentDescription = "Avaliação",
                modifier = Modifier
                    .clickable {
                        onRatingChange(i.toFloat())
                        currentRating = i.toFloat()
                    }
                    .size(24.dp),
                tint = Color.Yellow
            )
        }
    }
}