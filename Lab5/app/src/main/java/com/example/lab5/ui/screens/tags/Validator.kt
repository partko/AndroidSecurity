package com.example.lab5.ui.screens.tags

class Validator {
    private val MIN_NAME_LENGTH = 3

    fun validateName(name: String?): String? {
        return if (name?.isNotBlank() == true && name.length >= MIN_NAME_LENGTH) null
        else {
            "Имя устройства должно быть больше ${MIN_NAME_LENGTH}"
        }
    }

    fun validateModel(model: String?): String? {
        return if (model?.isNotBlank() == true && model.length >= MIN_NAME_LENGTH) null
        else {
            "Название модели должно быть больше ${MIN_NAME_LENGTH}"
        }
    }

    fun validateLocation(lon: String?, lat: String?): String? {
        return if (checkLocationValue(lon) && checkLocationValue(lat)) null
        else {
            "некорректное значение локации"
        }
    }

    private fun checkLocationValue(location: String?) = location?.isNotBlank() == true
            && location.toDoubleOrNull() != null
            && !location.contains(" ")
}