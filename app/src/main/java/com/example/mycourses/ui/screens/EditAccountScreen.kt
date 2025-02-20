import android.widget.DatePicker
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.mycourses.model.entities.User
import com.example.mycourses.navigation.AppDestination
import com.example.mycourses.ui.components.UserPicture
import com.example.mycourses.viewmodels.AccountViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun EditAccountScreen(
    navController: NavController,
    viewModel: AccountViewModel = hiltViewModel()
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        viewModel.initialize()
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(contentAlignment = Alignment.BottomEnd) {
                UserPicture(viewModel.editedUser, true) {
                    navController.navigate(AppDestination.UploadUserProfile.route)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            EditableField(
                value = viewModel.editedUser.name,
                onValueChange = { viewModel.updateName(it) },
                label = "Nome"
            )

            Spacer(modifier = Modifier.height(16.dp))

            EditableField(
                value = viewModel.editedUser.email,
                onValueChange = { viewModel.updateEmail(it) },
                label = "Email",
                readOnly = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
            )

            Spacer(modifier = Modifier.height(16.dp))

            EditableFieldDate(
                value = viewModel.editedUser.getFormattedAge(),
                onValueChange = { date -> viewModel.updateAge(date ?: Date()) },
                label = "Data de Nascimento"
            )

            Spacer(modifier = Modifier.height(32.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                Button(
                    onClick = {
                        viewModel.saveChanges(
                            onSuccess = {
                                scope.launch {
                                    snackbarHostState.showSnackbar("Perfil atualizado com sucesso")
                                }
                                navController.popBackStack()
                            },
                            onFailure = { exception ->
                                scope.launch {
                                    snackbarHostState.showSnackbar("Erro ao atualizar perfil: ${exception.message}")
                                }
                            }
                        )
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF007FFF))
                ) {
                    Text("Salvar", color = Color.White)
                }

                Spacer(modifier = Modifier.width(16.dp))

                Button(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)
                ) {
                    Text("Cancelar", color = Color.White)
                }
            }
        }
    }
}

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
        colors = OutlinedTextFieldDefaults.colors(
            cursorColor = Color.White,
            focusedBorderColor = Color(0xFF007FFF),
            unfocusedBorderColor = Color.LightGray,
        ),
        keyboardOptions = keyboardOptions,
        singleLine = true,
        textStyle = MaterialTheme.typography.bodyLarge.copy(
            fontWeight = FontWeight.Normal,
            fontSize = 18.sp
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
    val context = LocalContext.current

    val datePickerDialog = android.app.DatePickerDialog(
        context,
        { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
            val calendar = Calendar.getInstance()
            calendar.set(year, month, dayOfMonth)
            val data = calendar.time
            val formato = SimpleDateFormat("dd/MM/yyyy", Locale("pt", "BR"))
            dataNascimento = formato.format(data)
            onValueChange(data)
        },
        Calendar.getInstance().get(Calendar.YEAR),
        Calendar.getInstance().get(Calendar.MONTH),
        Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
    )

    TextField(
        value = dataNascimento,
        onValueChange = { },
        label = { Text(label) },
        modifier = Modifier
            .fillMaxWidth()
            .clickable { datePickerDialog.show() },
        readOnly = true,
        enabled = false,
        colors = TextFieldDefaults.colors(
            disabledTextColor = LocalContentColor.current,
            disabledContainerColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
            disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
        )
    )
}