import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuDefaults.outlinedTextFieldColors
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.mycourses.model.User

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditAccountScreen(
    navController: NavController,
    user: User
) {
    var editedUser by remember { mutableStateOf(user.copy()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            contentAlignment = Alignment.BottomEnd
        ) {
            UserInfo(editedUser)
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
    IconButton(
        onClick = {} , //onEditPictureClick,
        modifier = Modifier
            .offset(x = (36).dp, y = (-24).dp) // Ajuste o offset para sobrepor o círculo
            .clip(CircleShape) // Faz o ícone se ajustar ao círculo
            .size(28.dp)
            .background(Color.Gray, CircleShape) // Adiciona um fundo para o ícone
    ) {
        Icon(
            imageVector = Icons.Filled.Edit,
            contentDescription = "Editar foto",
            tint = Color.White
        )
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

private fun onSaveClick(editedUser: User) {

}
