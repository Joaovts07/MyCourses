import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.mycourses.model.entities.EnrolledCourse
import com.example.mycourses.model.entities.User
import com.example.mycourses.model.states.EnrolledCoursesState
import com.example.mycourses.ui.components.HighlighCourseCard
import com.example.mycourses.ui.components.UserPicture
import com.example.mycourses.viewmodels.AccountViewModel

@Composable
fun AccountScreen(
    onEditClick: (User) -> Unit,
    onCourseClicked: (EnrolledCourse?) -> Unit,
    accountViewModel: AccountViewModel = hiltViewModel()
) {
    val user = accountViewModel.user
    val uiState by accountViewModel.uiState.collectAsState()

    val isLoading = accountViewModel.isLoading
    val errorMessage = accountViewModel.errorMessage

    Column(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.systemBars)
            .padding(top = 18.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (isLoading) {
            CircularProgressIndicator()
        } else if (errorMessage != null) {
            Text(text = errorMessage, color = Color.Red)
        } else if (user != null) {
            UserInfo(user, onEditClick = onEditClick)
            Spacer(modifier = Modifier.height(16.dp))
        }

        when (uiState) {
            is EnrolledCoursesState.Loading -> {
                CircularProgressIndicator()
            }
            is EnrolledCoursesState.Success -> {
                val enrolledCourses = (uiState as EnrolledCoursesState.Success).enrolledCourses
                EnrolledCourses(enrolledCourses, onCourseClicked)
            }
            is EnrolledCoursesState.Error -> {
                val errorMessage = (uiState as EnrolledCoursesState.Error).message
                Text(text = errorMessage)
            }
        }
    }
}

@Composable
fun UserInfo(user: User, onEditClick: (User) -> Unit) {
    UserPicture(user, false)

    Spacer(modifier = Modifier.height(16.dp))

    Text(
        text = user.name,
        fontSize = 24.sp,
        fontWeight = FontWeight.Bold
    )

    Spacer(modifier = Modifier.height(8.dp))

    Text(
        text = user.email,
        fontSize = 16.sp,
        color = Color.Gray
    )

    Spacer(modifier = Modifier.height(16.dp))

    Text(
        text = "Idade: ${user.getYears()} Anos",
        fontSize = 16.sp
    )
    Spacer(modifier = Modifier.height(4.dp))

    Button(onClick = { onEditClick(user) },
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary
        )) {
        Text(text = "Editar Perfil")
    }
}

@Composable
fun EnrolledCourses(enrolledCourses: List<EnrolledCourse?>, onNavigateToDetails: (EnrolledCourse?) -> Unit) {
    Text(
        text = "Cursos Cadastrados",
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold,
        color = Color(0xFF007FFF)
    )

    Spacer(modifier = Modifier.height(12.dp))

    for (enrolledCourse in enrolledCourses) {
        HighlighCourseCard(
            course = enrolledCourse?.course,
            modifier = Modifier.clickable { onNavigateToDetails(enrolledCourse) }
        )
    }
}