import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.mycourses.model.Course
import com.example.mycourses.model.User
import com.example.mycourses.model.getCourse
import com.example.mycourses.ui.components.HighlighCourseCard
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

@Composable
fun AccountScreen() {
    var user by remember { mutableStateOf(User("", "", "", "")) }
    var enrolledCourses = remember { mutableStateListOf<Course>() }
    var isLoading by remember { mutableStateOf(true) }

    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return

    LaunchedEffect(userId) {
        val userDocRef = Firebase.firestore.collection("users").document(userId)
        userDocRef.get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    user = document.toObject(User::class.java)!!
                }
            }
            .addOnFailureListener { exception ->
                println("Erro ao buscar dados do usuário: ${exception.message}")
            }

        val subscriptionsRef = FirebaseFirestore.getInstance().collection("subscription")
        subscriptionsRef.whereEqualTo("userId", userId)
            .get()
            .addOnSuccessListener { subscriptionDocuments ->
                // Cria uma lista de courseIds a partir das inscrições
                val courseIds = mutableListOf<String>()
                for (subscription in subscriptionDocuments) {
                    val courseId = subscription.getString("courseId")
                    if (courseId != null) {
                        courseIds.add(courseId)
                    }
                }

                // Agora, para cada courseId, obtenha os detalhes do curso
                val coursesRef = FirebaseFirestore.getInstance().collection("courses")
                for (courseId in courseIds) {
                    coursesRef.document(courseId).get()
                        .addOnSuccessListener { courseDocument ->
                            enrolledCourses.add(getCourse(courseDocument))


                        }
                }
                isLoading = false
            }
            .addOnFailureListener { exception ->
                // Trate erros aqui, como falha na consulta
                Log.e("Firestore", "Erro ao buscar inscrições", exception)
            }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (isLoading) {
            CircularProgressIndicator()
        } else {
            // Mostrar informações do usuário e cursos
            UserInfo(user)
            Spacer(modifier = Modifier.height(16.dp))
            EnrolledCourses(enrolledCourses)
        }
    }
}

@Composable
fun UserInfo(user: User) {
    if (user.profilePictureUrl.isNotEmpty()) {
        AsyncImage(
            model = user.profilePictureUrl,
            contentDescription = "Foto do perfil",
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
        )
    } else {
        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .background(Color.Blue),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = user.name.first().toString(),
                fontSize = 40.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }

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
        text = "Idade: ${user.age}",
        fontSize = 16.sp
    )
}

@Composable
fun EnrolledCourses(enrolledCourses: List<Course>) {
    Text(
        text = "Cursos Cadastrados",
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold,
        color = Color(0xFF007FFF)
    )

    Spacer(modifier = Modifier.height(12.dp))

    for (course in enrolledCourses) {
        HighlighCourseCard(course)
    }
}