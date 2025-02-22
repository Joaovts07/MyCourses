import androidx.compose.foundation.BorderStroke
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
import androidx.navigation.NavController
import com.example.mycourses.model.entities.Course
import com.example.mycourses.model.entities.EnrolledCourse
import com.example.mycourses.model.entities.User
import com.example.mycourses.model.states.AccountUiState
import com.example.mycourses.navigation.AppDestination
import com.example.mycourses.ui.components.HighlighCourseCard
import com.example.mycourses.ui.components.UserPicture
import com.example.mycourses.viewmodels.AccountViewModel

@Composable
fun AccountScreen(
    navController: NavController,
    onEditClick: (User) -> Unit,
    onLogout: () -> Unit,
    accountViewModel: AccountViewModel = hiltViewModel()
) {
    val uiState by accountViewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.systemBars)
            .padding( start = 16.dp, end = 16.dp, top = 18.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LaunchedEffect(accountViewModel.logoutEvent) {
            accountViewModel.logoutEvent.collect { onLogout() }
        }


        when (uiState) {
            is AccountUiState.Loading -> CircularProgressIndicator()
            is AccountUiState.Success -> {
                UserInfo((uiState as AccountUiState.Success).user, onEditClick = onEditClick)
                Spacer(modifier = Modifier.height(16.dp))
                val enrolledCourses = (uiState as AccountUiState.Success).enrolledCourses
                val myCourses = (uiState as AccountUiState.Success).myCourses
                EnrolledCourses(enrolledCourses, navController)

                Spacer(Modifier.height(16.dp))

                MyCourses(myCourses, navController)

                Spacer(Modifier.height(16.dp))

                CreateCourseButton { navigateToCourse(navController) }
                Spacer(Modifier.height(16.dp))
                LogoutButton { accountViewModel.logout() }

            }
            is AccountUiState.Error -> {
                val errorMessage = (uiState as AccountUiState.Error).message
                Text(text = errorMessage)
            }

        }
    }
}

private fun navigateToCourse(navController: NavController) {
    navController.navigate(AppDestination.CourseInfoCreation.route)
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
fun EnrolledCourses(enrolledCourses: List<EnrolledCourse?>, navController: NavController) {
    Text(
        text = "Inscrições",
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold,
        color = Color(0xFF007FFF)
    )

    Spacer(modifier = Modifier.height(12.dp))

    for (enrolledCourse in enrolledCourses) {
        HighlighCourseCard(
            course = enrolledCourse?.course,
            modifier = Modifier.clickable {
                navController.navigate("${AppDestination.CourseDetails.route}/${enrolledCourse?.course?.id}")
            }
        )
    }
}

@Composable
fun MyCourses(courses: List<Course>, navController: NavController) {
    Text(
        text = "Meus Cursos",
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold,
        color = Color(0xFF007FFF)
    )

    Spacer(modifier = Modifier.height(12.dp))

    for (course in courses) {
        HighlighCourseCard(
            course = course,
            modifier = Modifier.clickable {
                navController.navigate("${AppDestination.CourseDetails.route}/${course.id}")
            }
        )
    }
}

@Composable
fun LogoutButton(onclick: () -> Unit) {
    OutlinedButton(onClick = { onclick()  },
        border = BorderStroke(2.dp, MaterialTheme.colorScheme.error),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.error
        )) {
        Text(text = "Sair")
    }
}

@Composable
fun CreateCourseButton(onclick: () -> Unit) {
    Button(onClick = { onclick() },
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary
        )) {
        Text(text = "Cadastrar Curso")
    }
}