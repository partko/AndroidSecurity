package com.example.lab5.ui.screens.tags

import android.app.Application
import android.content.ContentResolver
import android.net.Uri
import androidx.exifinterface.media.ExifInterface
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class TagsViewModel(application: Application) : AndroidViewModel(application) {
    private val _uiState: MutableStateFlow<TagsUiState> = MutableStateFlow(TagsUiState())
    val uiState: StateFlow<TagsUiState> = _uiState.asStateFlow()
    private val _uiCheckBoxState: MutableStateFlow<TagsCheckBoxUiState> =
        MutableStateFlow(TagsCheckBoxUiState())
    val uiCheckBoxState: StateFlow<TagsCheckBoxUiState> = _uiCheckBoxState.asStateFlow()

    private val _errorSF: MutableSharedFlow<String?> = MutableSharedFlow()
    val errorSF: SharedFlow<String?> = _errorSF.asSharedFlow()
    var showError = false

    private val _navigateToPictureScreen = MutableStateFlow(false)
    val navigateToPictureScreen: Flow<Boolean>
        get() = _navigateToPictureScreen

    private lateinit var uri: Uri

    fun onDateChange(newValue: String) {
        _uiState.value = _uiState.value.copy(date = newValue)
    }

    fun onLatitudeChange(newValue: String) {
        _uiState.value = _uiState.value.copy(latitude = newValue)
    }

    fun onLongitudeChange(newValue: String) {
        _uiState.value = _uiState.value.copy(longitude = newValue)
    }

    fun onPhoneNameChange(newValue: String) {
        _uiState.value = _uiState.value.copy(phoneName = newValue)
    }

    fun onPhoneModelChange(newValue: String) {
        _uiState.value = _uiState.value.copy(phoneModel = newValue)
    }

    fun initUiState(
        uri: Uri,
        date: String?,
        latitude: String?,
        longitude: String?,
        phoneName: String?,
        phoneModel: String?
    ) {
        this.uri = uri



        _uiState.value =
            TagsUiState(
                getCorrectDate(date),
                latitude,
                longitude,
                phoneName,
                phoneModel
            )
    }

    private fun getCorrectDate(date: String?): String? {
        val f = DateTimeFormatter.ofPattern("yyyy:MM:dd HH:mm:ss")
        val localDateTime = LocalDateTime.parse(date, f)
        return localDateTime.format(formatter)
    }


    fun onCheckPhoneNameChange() {
        _uiCheckBoxState.value =
            _uiCheckBoxState.value.copy(phoneName = !_uiCheckBoxState.value.phoneName)
    }

    fun onCheckModelChange() {
        _uiCheckBoxState.value =
            _uiCheckBoxState.value.copy(phoneModel = !_uiCheckBoxState.value.phoneModel)
    }

    fun onCheckLocationChange() {
        _uiCheckBoxState.value =
            _uiCheckBoxState.value.copy(location = !_uiCheckBoxState.value.location)
    }

    fun onCheckDateChange() {
        _uiCheckBoxState.value = _uiCheckBoxState.value.copy(date = !_uiCheckBoxState.value.date)
    }

    fun hasChanges() = _uiCheckBoxState.value.run {
        date || location || phoneModel || phoneName
    }

    private fun validateFields(): Boolean {
        val validator = Validator()
        if (_uiCheckBoxState.value.phoneName) {
            val nameRes = validator.validateName(_uiState.value.phoneName)
            if (nameRes != null) {
                showError = true
                viewModelScope.launch {
                    _errorSF.emit(nameRes)
                }
                return false
            }
        }
        if (_uiCheckBoxState.value.phoneModel) {
            val modelRes = validator.validateModel(_uiState.value.phoneModel)
            if (modelRes != null) {
                showError = true
                viewModelScope.launch {
                    _errorSF.emit(modelRes)
                }
                return false
            }
        }
        if (_uiCheckBoxState.value.location) {
            val locRes =
                validator.validateLocation(_uiState.value.longitude, _uiState.value.latitude)
            if (locRes != null) {
                showError = true
                viewModelScope.launch {
                    _errorSF.emit(locRes)
                }
                return false
            }
        }
        return true
    }

    fun saveTags(contentResolver: ContentResolver, newDate: LocalDateTime?) {
        if (!validateFields()) return
        contentResolver.openFileDescriptor(uri, "rw", null)
            ?.use { fileDescriptor ->
                val exif = ExifInterface(fileDescriptor.fileDescriptor)

                if (_uiCheckBoxState.value.date) {
                    val date = newDate?.format(formatter)
                    exif.setAttribute(ExifInterface.TAG_DATETIME, date)
                }

                if (_uiCheckBoxState.value.phoneName) {
                    val name = _uiState.value.phoneName
                    exif.setAttribute(ExifInterface.TAG_MAKE, name)
                }

                if (_uiCheckBoxState.value.phoneModel) {
                    val model = _uiState.value.phoneModel
                    exif.setAttribute(ExifInterface.TAG_MODEL, model)
                }

                if (_uiCheckBoxState.value.location) {
                    val lat = _uiState.value.latitude
                    exif.setAttribute(ExifInterface.TAG_GPS_LATITUDE, lat)

                    val lon = _uiState.value.longitude
                    exif.setAttribute(ExifInterface.TAG_GPS_LONGITUDE, lon)
                }
                exif.saveAttributes()
            }
        _navigateToPictureScreen.value = true
    }


    val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss")

}