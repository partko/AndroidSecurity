package com.example.lab5.ui.screens.main

import android.app.Application
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel

class MainViewModel(application: Application) : AndroidViewModel(application) {
    val collection =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Video.Media.getContentUri(
                MediaStore.VOLUME_EXTERNAL
            )
        } else {
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI
        }

    private var _imageUri: MutableState<Uri?> = mutableStateOf(null)
    var imageUri: State<Uri?> = _imageUri
    lateinit var encodedUri: String

    fun setImage(uri: Uri) {
        _imageUri.value = uri
    }
}