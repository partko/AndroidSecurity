package com.example.lab5.ui.screens.tags

import android.content.Context
import android.content.ContextWrapper
import android.content.res.Configuration
import android.media.ExifInterface
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.commandiron.wheel_picker_compose.WheelDateTimePicker
import com.example.mediastore_exifinterface_example.ui.composable.BasicCustomTextField
import com.google.android.material.datepicker.MaterialDatePicker
import java.time.LocalDateTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TagsScreen(
    vm: TagsViewModel = viewModel(),
    uri: Uri,
    navController: NavHostController
) {
    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(Unit) {
        val contentResolver = context.contentResolver
        contentResolver.openFileDescriptor(uri, "w", null)
            ?.use { fileDescriptor ->
                val exif = androidx.exifinterface.media.ExifInterface(fileDescriptor.fileDescriptor)
                val date = exif.getAttribute(ExifInterface.TAG_DATETIME)
                val latitude = exif.getAttribute(ExifInterface.TAG_GPS_LATITUDE)
                val longitude = exif.getAttribute(ExifInterface.TAG_GPS_LONGITUDE)
                val phoneModel = exif.getAttribute(ExifInterface.TAG_MODEL)
                val phoneName = exif.getAttribute(ExifInterface.TAG_MAKE)
                vm.initUiState(uri, date, latitude, longitude, phoneName, phoneModel)
            }
    }
    LaunchedEffect(Unit) {
        vm.navigateToPictureScreen.collect { navigate ->
            if (navigate) {
                navController.popBackStack()
            }
        }
    }
    LaunchedEffect(Unit) {
        vm.errorSF.collect { error ->
            if (!vm.showError || error == null) return@collect
            Log.i("kpop", error)
            Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
        }
    }


    val uiState by vm.uiState.collectAsStateWithLifecycle()
    val uiCheckBoxState by vm.uiCheckBoxState.collectAsStateWithLifecycle()

    val isDarkTheme = (context.resources.configuration.uiMode and
            Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES

    val textColor = if (isDarkTheme) {
        Color.White
    } else {
        Color.Black
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .fillMaxWidth()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),

        ) {
        Text(
            text = uiState.date ?: "Укажите время",
            style = TextStyle(
                color = textColor,
                fontSize = 26.sp,
                fontFamily = FontFamily.Cursive
            ),
            modifier = Modifier.padding(top = 10.dp)
        )
        var newDate: LocalDateTime? = null
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            WheelDateTimePicker { snappedDateTime -> newDate = snappedDateTime }
            Checkbox(checked = uiCheckBoxState.date, onCheckedChange = { vm.onCheckDateChange() })
        }
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(5.dp)) {
            BasicCustomTextField(value = uiState.phoneName ?: "",
                label = "Устройство",
                onValueChange = { vm.onPhoneNameChange(it) })
            Checkbox(
                checked = uiCheckBoxState.phoneName,
                onCheckedChange = { vm.onCheckPhoneNameChange() })
        }
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(5.dp)) {
            BasicCustomTextField(value = uiState.phoneModel ?: "",
                label = "Модель",
                onValueChange = { vm.onPhoneModelChange(it) })
            Checkbox(
                checked = uiCheckBoxState.phoneModel,
                onCheckedChange = { vm.onCheckModelChange() })
        }

        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(5.dp)) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                BasicCustomTextField(
                    value = uiState.latitude ?: "",
                    label = "Широта",
                    onValueChange = { vm.onLatitudeChange(it) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                )
                BasicCustomTextField(
                    value = uiState.longitude ?: "",
                    label = "Долгота",
                    onValueChange = { vm.onLongitudeChange(it) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }

            Checkbox(
                checked = uiCheckBoxState.location,
                onCheckedChange = { vm.onCheckLocationChange() })
        }
        Button(
            onClick = {
                keyboardController?.hide()
                vm.saveTags(context.contentResolver, newDate)
            },
            enabled = vm.hasChanges()
        ) {
            Text(text = "Сохранить")
        }


//        OutlinedTextField(
//            value = value,
//            onValueChange = {
//                value = it
//                onValueChanged(it)
//            },
//            label = { Text(label) },
//            leadingIcon = { Icon(icon, contentDescription = label) },
//            keyboardOptions = KeyboardOptions.Default.copy(
//                imeAction = ImeAction.Done
//            ),
//            keyboardActions = KeyboardActions(
//                onDone = {
//                    keyboardController?.hide()
//                }
//            ),
//            modifier = Modifier
//                .fillMaxWidth()
//                .height(56.dp)
//        )
    }
}

private fun showDatePicker(activity: FragmentActivity) {
    val picker = MaterialDatePicker.Builder.datePicker().build()
    activity.let {
        picker.show(it.supportFragmentManager, picker.toString())
        picker.addOnPositiveButtonClickListener {

        }
    }
}

fun Context.getActivity(): ComponentActivity? = when (this) {
    is ComponentActivity -> this
    is ContextWrapper -> baseContext.getActivity()
    else -> null
}