package com.example.lab5.ui.screens.main

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.lab5.AppRoutes
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(vm: MainViewModel = viewModel(), navController: NavHostController) {
    Scaffold(content = {
        val imageData: MutableState<Uri?> = remember { mutableStateOf(null) }
        val context = LocalContext.current
        val launcher =
            //rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) {
            rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                imageData.value = it.data!!.data
            }
        Column(
            modifier = Modifier.padding(16.dp),
            content = {

                imageData.let {
                    val bitmap: MutableState<Bitmap?> = remember { mutableStateOf(null) }
                    val uri = it.value
                    if (uri != null) {
                        if (Build.VERSION.SDK_INT < 28) {
                            bitmap.value = MediaStore.Images
                                .Media.getBitmap(context.contentResolver, uri)

                        } else {
                            val source = ImageDecoder
                                .createSource(context.contentResolver, uri)
                            bitmap.value = ImageDecoder.decodeBitmap(source)
                        }

                        bitmap.value?.let { btm ->
                            Image(
                                bitmap = btm.asImageBitmap(),
                                contentDescription = null,
                                modifier = Modifier.size(400.dp)
                            )
                        }
                        val contentResolver = context.contentResolver
                        Log.d("debug", "$uri")
                        contentResolver.openFileDescriptor(uri, "r", null)
                            ?.use { fileDescriptor ->
                                val exif = ExifInterface(fileDescriptor.fileDescriptor)


                                exif.getAttribute(ExifInterface.TAG_DATETIME)?.let {
                                    Text("Дата: $it")
                                }
                                exif.getAttribute(ExifInterface.TAG_MAKE)?.let {
                                    Text("Устройство: $it")
                                }
                                exif.getAttribute(ExifInterface.TAG_MODEL)?.let {
                                    Text("Модель: $it")
                                }
                                exif.getAttribute(ExifInterface.TAG_GPS_LATITUDE)?.let {
                                    Text("Широта: $it")
                                }
                                exif.getAttribute(ExifInterface.TAG_GPS_LONGITUDE)?.let {
                                    Text("Долгота: $it")
                                }
                            }

                    }

                }

                Spacer(modifier = Modifier.weight(1f))
                Button(onClick = {
//                    launcher.launch(
//                        "image/*"
//                    )
                    val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
                    intent.type = "image/*";
                    launcher.launch(intent)
                }, content = {
                    Text(text = "Select Image From Gallery")
                })
            })
    })

}