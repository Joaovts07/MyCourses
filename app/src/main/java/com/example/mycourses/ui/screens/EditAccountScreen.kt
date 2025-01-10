import android.widget.DatePicker
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.material3.ExposedDropdownMenuDefaults.outlinedTextFieldColors
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.mycourses.model.User
import com.example.mycourses.ui.components.UserPicture
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

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
            UserPicture(editedUser, true)
        }

        Spacer(modifier = Modifier.height(16.dp))

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
            readOnly = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
        )

        Spacer(modifier = Modifier.height(16.dp))
        EditableFieldDate(
            value = editedUser.getFormattedAge() ?: "",
            onValueChange = { date ->
                editedUser = editedUser.copy(age = date ?: Date())
            },
            label = "Idade"
        )

        Spacer(modifier = Modifier.height(32.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            Button(
                onClick = { onEditClick(editedUser) },
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

fun onEditClick(editedUser: User) {

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditableField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    readOnly: Boolean = false,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default
) {
    OutlinedTextField(
        value = value,
        readOnly = readOnly,
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

@Composable
fun EditableFieldDate(
    value: String,
    onValueChange: (Date?) -> Unit,
    label: String
) {
    var dataNascimento by remember { mutableStateOf(value) }
    var dataTimestamp by remember { mutableStateOf<Date?>(null) }
    val context = LocalContext.current
    val calendar = Calendar.getInstance()

    val datePickerDialog = android.app.DatePickerDialog(
        context,
        { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
            calendar.set(year, month, dayOfMonth)
            val data = calendar.time
            val formato = SimpleDateFormat("dd/MM/yyyy", Locale("pt", "BR"))
            dataNascimento = formato.format(data)
            onValueChange(dataTimestamp)
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    TextField(
        value = dataNascimento,
        onValueChange = { }, // Impede a edição direta
        label = { Text(label) },
        modifier = Modifier.clickable { datePickerDialog.show() }
    )
}