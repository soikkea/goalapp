package com.github.soikkea.goalapp.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.github.soikkea.goalapp.ui.theme.GoalAppTheme

@Composable
fun GoalApp(
    darkTheme: Boolean = true
) {
    GoalAppTheme(darkTheme = darkTheme) {
        val navController = rememberNavController()
        // A surface container using the 'background' color from the theme
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            GoalAppNavGraph(navController = navController)
        }
    }
}