package com.example.mycourses.model.states

import android.net.Uri

data class CourseCreationUiState(
    val title: String = "",
    val category: String = "",
    val imageUri: Uri? = null
)
