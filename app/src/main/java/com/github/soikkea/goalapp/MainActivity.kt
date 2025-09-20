package com.github.soikkea.goalapp

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES
import androidx.appcompat.app.AppCompatDelegate.setDefaultNightMode
import com.github.soikkea.goalapp.ui.GoalApp
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Set dark theme by default
        // TODO: Make dark theme user configurable?
        setDefaultNightMode(MODE_NIGHT_YES)
        setContent {
            GoalApp(darkTheme = true)
        }
    }
}