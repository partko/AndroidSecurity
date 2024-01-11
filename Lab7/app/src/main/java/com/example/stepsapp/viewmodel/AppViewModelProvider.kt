package com.example.stepsapp.viewmodel

import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.stepsapp.BaseApplication

object AppViewModelProvider {
    val Factory = viewModelFactory {
        initializer {
            MainScreenViewModel(stepsApplication().healthConnectManager)
        }
    }
}

fun CreationExtras.stepsApplication():BaseApplication =
    (this[AndroidViewModelFactory.APPLICATION_KEY] as BaseApplication)
