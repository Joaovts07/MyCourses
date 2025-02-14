package com.example.mycourses.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.mycourses.viewmodels.CourseCreationStep
import com.example.mycourses.viewmodels.CourseCreationViewModel

@Composable
fun CourseCreationScreen(
    navController: NavController,
    viewModel: CourseCreationViewModel = hiltViewModel(),
    ) {

    val currentStep by remember { mutableStateOf(viewModel.currentStep) }

    when (currentStep) {
        CourseCreationStep.Info -> CourseInfoScreen { navController.navigate("") }
        CourseCreationStep.Image -> {
            CourseImageScreen(
                onNext = { navController.navigate("")},
                onBack = { navController.navigate("") }
            )
        }
        CourseCreationStep.Review ->  {
            CourseReviewScreen(
                onSubmit = { navController.navigate("")},
                onBack = { navController.navigate("")})
        }
    }
}