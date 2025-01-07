import androidx.compose.foundation.background
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
            .background(Color(0xFF283593))
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

        // Campos editáveis com design clean
        EditableField(
            value = editedUser.name,
            onValueChange = { editedUser = editedUser.copy(name = it) },
            label = "Nome"
        )

        Spacer(modifier = Modifier.height(16.dp))

        EditableField(
            value = editedUser.email,
            onValueChange = { editedUser = editedUser.copy(email = it) },
            label = "Email",
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
        )

        Spacer(modifier = Modifier.height(16.dp))

        EditableField(
            value = editedUser.getFormattedAge() ?: "",
            onValueChange = { editedUser = editedUser.copy(age = it) },
            label = "Idade",
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Botões de salvar e cancelar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            Button(
                onClick = { /* Salvar as alterações */ },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF007FFF)
                )
            ) {
                Text("Salvar", color = Color.White)
            }

            Spacer(modifier = Modifier.width(16.dp))

            Button(
                onClick = { navController.popBackStack() },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Gray
                )
            ) {
                Text("Cancelar", color = Color.White)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditableField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, color = Color.White) },
        modifier = Modifier.fillMaxWidth(),
        colors = outlinedTextFieldColors(
            cursorColor = Color.White,
            focusedBorderColor = Color(0xFF007FFF), // Azul claro
            unfocusedBorderColor = Color.LightGray
        ),
        keyboardOptions = keyboardOptions,
        singleLine = true, // Garante que o campo seja de linha única
        textStyle = MaterialTheme.typography.bodyLarge.copy(
            fontWeight = FontWeight.Normal, // Define o peso da fonte como normal
            fontSize = 18.sp // Define o tamanho da fonte
        )
    )
}