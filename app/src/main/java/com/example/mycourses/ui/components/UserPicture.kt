package com.example.mycourses.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.mycourses.model.entities.User

@Composable
fun UserPicture(user: User, isEditable: Boolean, onEditPictureClick:(User) -> Unit = {}) {
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
    if (isEditable) {
        IconButton(
            onClick = {onEditPictureClick(user)} ,
            modifier = Modifier
                .offset(x = (4).dp, y = (4).dp)
                .clip(CircleShape)
                .size(28.dp)
                .background(Color.Gray, CircleShape)
        ) {
            Icon(
                imageVector = Icons.Filled.Edit,
                contentDescription = "Editar foto",
                tint = Color.White
            )
        }
    }

}