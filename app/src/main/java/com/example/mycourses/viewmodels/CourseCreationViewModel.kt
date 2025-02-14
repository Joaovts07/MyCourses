package com.example.mycourses.viewmodels

sealed class CourseCreationStep {
    object Info : CourseCreationStep()
    object Image : CourseCreationStep()
    object Review : CourseCreationStep()
}

@HiltViewModel
class CourseCreationViewModel @Inject constructor(
    private val repository: CourseRepository
) : ViewModel() {
    var uiState by mutableStateOf(CourseCreationUiState())
        private set

    var currentStep by mutableStateOf<CourseCreationStep>(CourseCreationStep.Info)
        private set

    fun updateTitle(title: String) {
        uiState = uiState.copy(title = title)
    }

    fun updateCategory(category: String) {
        uiState = uiState.copy(category = category)
    }

    fun updateImage(imageUri: Uri?) {
        uiState = uiState.copy(imageUri = imageUri)
    }

    fun nextStep() {
        currentStep = when (currentStep) {
            CourseCreationStep.Info -> CourseCreationStep.Image
            CourseCreationStep.Image -> CourseCreationStep.Review
            CourseCreationStep.Review -> CourseCreationStep.Review
        }
    }

    fun previousStep() {
        currentStep = when (currentStep) {
            CourseCreationStep.Review -> CourseCreationStep.Image
            CourseCreationStep.Image -> CourseCreationStep.Info
            CourseCreationStep.Info -> CourseCreationStep.Info
        }
    }

    fun submitCourse() {
        viewModelScope.launch {
            repository.createCourse(uiState.title, uiState.category, uiState.imageUri)
        }
    }
}

@Composable
fun CourseCreationScreen(viewModel: CourseCreationViewModel = hiltViewModel()) {
    val uiState by remember { mutableStateOf(viewModel.uiState) }
    val currentStep by remember { mutableStateOf(viewModel.currentStep) }

    when (currentStep) {
        CourseCreationStep.Info -> CourseInfoScreen(viewModel)
        CourseCreationStep.Image -> CourseImageScreen(viewModel)
        CourseCreationStep.Review -> CourseReviewScreen(viewModel)
    }
}