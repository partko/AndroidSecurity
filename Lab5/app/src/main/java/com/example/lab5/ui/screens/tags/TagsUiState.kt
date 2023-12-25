package com.example.lab5.ui.screens.tags

data class TagsUiState(
    val date: String? = "",
    val latitude: String? = "",
    val longitude: String? = "",
    val phoneName: String? = "",
    val phoneModel: String? = "",
)

data class TagsCheckBoxUiState(
    val date: Boolean = false,
    val location: Boolean = false,
    val phoneName: Boolean = false,
    val phoneModel: Boolean = false,
)