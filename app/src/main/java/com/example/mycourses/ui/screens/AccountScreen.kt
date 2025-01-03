import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.mycourses.model.User

@Composable
fun AccountScreen(user: User) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Foto do usuário (se disponível)
        AsyncImage(
            model = user.profilePictureUrl, // Substitua pelo campo da foto do usuário
            contentDescription = "Foto do perfil",
            modifier = Modifier.size(100.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Nome do usuário
        Text(
            text = user.name,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Email do usuário
        Text(
            text = user.email,
            fontSize = 16.sp,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Idade do usuário (se disponível)
        Text(
            text = "Idade: ${user.age}",
            fontSize = 16.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Cursos cadastrados
        Text(
            text = "Cursos Cadastrados",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF007FFF) // Azul
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Lista de cursos (substitua pela sua lógica de exibição)
        /*for (course in user.enrolledCourses) {
            Text(text = course.name)
        }*/

        // ... adicione outras informações e funcionalidades da conta ...
    }
}