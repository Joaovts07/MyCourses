package com.example.mycourses.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.mycourses.R


// Dados dos cursos (substitua por seus dados reais)
data class Curso(val nome: String, val imagem: Int)

val cursos = listOf(
    Curso("Kotlin para Iniciantes", R.drawable.ic_launcher_background),
    Curso("Desenvolvimento Android", R.drawable.ic_launcher_background),
    Curso("Compose para iniciantes", R.drawable.ic_launcher_background),
    // Adicione mais cursos aqui...
)

@Composable
fun TelaInicial(navController: NavHostController) {
    var selectedItem by remember { mutableIntStateOf(0) }
    val items = listOf("Meus Cursos", "Principal", "Conta")

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = Color(0xFFADD8E6) // Azul claro
            ){
                items.forEachIndexed { index, item ->
                    NavigationBarItem(
                        icon = {
                            when (index) {
                                0 -> Icon(Icons.Filled.Call, contentDescription = "Cursos")
                                1 -> Icon(Icons.Filled.Home, contentDescription = "Principal")
                                2 -> Icon(Icons.Filled.AccountCircle, contentDescription = "Conta")
                                else -> {}
                            }
                        },
                        label = { Text(item) },
                        selected = selectedItem == index,
                        onClick = {
                            selectedItem = index
                            when (index) {
                                0 -> navController.navigate("meus_cursos")
                                1 -> navController.navigate("principal")
                                2 -> navController.navigate("conta")
                            }
                        }
                    )
                }
            }
        }
    ) { paddingValues ->
        Navigation(navController, paddingValues)
    }
}

@Composable
fun Navigation(navController: NavHostController, paddingValues: PaddingValues) {

    NavHost(navController = navController, startDestination = "meus_cursos") {
        composable("meus_cursos") { MeusCursos(paddingValues) }
        composable("principal") { Principal(paddingValues) }
        composable("conta") { Conta(paddingValues) }
    }
}

@Composable
fun MeusCursos(paddingValues: PaddingValues) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier
            .padding(paddingValues)
            .fillMaxSize()
            .background(Color(0xFFF0F8FF)), // Azul claro mais suave
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(cursos) { curso ->
            Card(
                //elevation = CardElevation 4.dp
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        bitmap = LocalContext.current.resources.openRawResource(curso.imagem) as ImageBitmap,
                        contentDescription = curso.nome,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp),
                        contentScale = ContentScale.Crop
                    )
                    Text(curso.nome, modifier = Modifier.padding(8.dp))
                }
            }
        }
    }
}

@Composable
fun Principal(paddingValues: PaddingValues) {
    Text(text = "Principal", modifier = Modifier.padding(paddingValues))
}

@Composable
fun Conta(paddingValues: PaddingValues) {
    Text(text = "Conta", modifier = Modifier.padding(paddingValues))
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    val navController = rememberNavController()
    TelaInicial(navController)
}