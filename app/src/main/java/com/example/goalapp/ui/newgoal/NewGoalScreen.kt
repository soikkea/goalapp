package com.example.goalapp.ui.newgoal

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.goalapp.ui.GoalApp
import com.example.goalapp.ui.components.TopAppBarWithBackButton
import com.example.goalapp.ui.theme.GoalAppTheme

@Composable
fun NewGoalScreen(
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBarWithBackButton(title = "Add goal")
        },
    ) { contentPadding ->
        Column(
            modifier = Modifier.padding(contentPadding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                onClick = { onSaveClick(onBack) },
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth()
            ) {
                Text(text = "Save")
            }
        }
    }
}

private fun onSaveClick(onFinish: () -> Unit) {
    // TODO
    onFinish()
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    GoalAppTheme {
        NewGoalScreen(onBack = {})
    }
}
