import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.material3.ExposedDropdownMenuDefaults.outlinedTextFieldColors
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.mycourses.model.User

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditAccountScreen(
    navController: NavController,
    user: User
) {
    var editedUser by remember { mutableStateOf(user.copy()) } // Cria uma cópia editável do usuário

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Foto do perfil com ícone de editar
        Box(
            contentAlignment = Alignment.BottomEnd
        ) {
            AsyncImage(
                model = editedUser.profilePictureUrl,
                contentDescription = "Foto do perfil",
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
            )

            IconButton(
                onClick = { /* Abrir tela para trocar a foto */ },
                modifier = Modifier
                    .offset(x = (-12).dp, y = (-12).dp)
                    .clip(CircleShape)
                    .size(32.dp)
                    .background(Color.LightGray, CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Filled.Edit,
                    contentDescription = "Editar foto",
                    tint = Color.Red
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Campos editáveis
        OutlinedTextField(
            value = editedUser.name,
            onValueChange = { editedUser = editedUser.copy(name = it) },
            label = { Text("Nome", color = Color.White) },
            modifier = Modifier.fillMaxWidth(),
            colors = outlinedTextFieldColors(
                unfocusedBorderColor = Color.LightGray,
                focusedBorderColor = Color.White
            )
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = editedUser.email,
            onValueChange = { editedUser = editedUser.copy(email = it) },
            label = { Text("Email", color = Color.White) },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            colors = outlinedTextFieldColors(
                unfocusedBorderColor = Color.LightGray,
                focusedBorderColor = Color.White
            )
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = editedUser.age ?: "", // Lidar com a possibilidade de idade ser nula
            onValueChange = { editedUser = editedUser.copy(age = it) },
            label = { Text("Idade", color = Color.White) },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            colors = outlinedTextFieldColors(
                unfocusedBorderColor = Color.LightGray,
                focusedBorderColor = Color.White
            )
        )

        // ... outros campos editáveis ...

        Spacer(modifier = Modifier.height(16.dp))

        // Botões de salvar e cancelar
        Row {
            Button(
                onClick = { onSaveClick(editedUser) }, // Chama a função para salvar as alterações
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF007FFF) // Cor do botão azul claro
                )
            ) {
                Text("Salvar", color = Color.White)
            }

            Spacer(modifier = Modifier.width(16.dp))

            Button(
                onClick = { navController.popBackStack()  },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Gray // Cor do botão cinza
                )
            ) {
                Text("Cancelar", color = Color.White)
            }
        }
    }
}

private fun onSaveClick(editedUser: User) {

}
