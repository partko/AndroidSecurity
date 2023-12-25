package com.example.lab5

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.lab5.ui.screens.main.MainScreen


@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = AppRoutes.PictureScreen.name
    ) {

        composable(AppRoutes.PictureScreen.name) {
            MainScreen(navController = navController)
        }
//        composable("${AppRoutes.EditorScreen.name}/{uriPath}") { backStackEntry ->
//
//            val uriPath = backStackEntry.arguments?.getString("uriPath").toString()
//            val uri = Uri.parse(uriPath)
//            EditorScreen(uri = uri, navController = navController)
//        }
    }
}

enum class AppRoutes {
    PictureScreen, EditorScreen
}