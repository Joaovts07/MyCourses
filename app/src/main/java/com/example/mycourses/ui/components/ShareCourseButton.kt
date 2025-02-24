package com.example.mycourses.ui.components

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.FileProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL

@Composable
fun ShareCourseButton(
    courseName: String,
    courseDescription: String,
    courseLink: String,
    courseImageUrl: String?
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    IconButton(onClick = {
        scope.launch {
            try {
                val intent = Intent(Intent.ACTION_SEND).apply {
                    val shareText = buildString {
                        append(courseName).append("\n")
                        append(courseDescription).append("\n")
                        append("Confira este curso incrível: ").append(courseLink)
                    }
                    putExtra(Intent.EXTRA_TEXT, shareText)
                    putExtra(Intent.EXTRA_SUBJECT, courseName)
                    type = "text/plain"

                    courseImageUrl?.let { url ->
                        val imageUri = downloadImageAndGetUri(context, url)
                        if (imageUri != null) {
                            putExtra(Intent.EXTRA_STREAM, imageUri)
                            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                            type = "image/*"
                        }
                    }
                }

                val shareIntent = Intent.createChooser(intent, "Compartilhar curso")
                context.startActivity(shareIntent)

            } catch (e: Exception) {
                Log.e("ShareCourseButton", "Erro ao compartilhar curso", e)
            }
        }
    }) {
        Icon(imageVector = Icons.Filled.Share, contentDescription = "Compartilhar")
    }
}

private suspend fun downloadImageAndGetUri(context: Context, imageUrl: String): Uri? {
    return withContext(Dispatchers.IO) {
        try {
            val url = URL(imageUrl)
            val connection = url.openConnection() as HttpURLConnection
            connection.doInput = true
            connection.connect()

            val inputStream = connection.inputStream
            val cacheFile = File(context.cacheDir, "shared_course_image.jpg")
            val outputStream = FileOutputStream(cacheFile)

            inputStream.copyTo(outputStream)
            inputStream.close()
            outputStream.close()

            FileProvider.getUriForFile(
                context,
                "${context.packageName}.provider",
                cacheFile
            )
        } catch (e: Exception) {
            Log.e("ShareCourseButton", "Erro ao baixar imagem", e)
            null
        }
    }
}