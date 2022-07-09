package com.example.goalapp.ui.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.goalapp.ui.goallist.GoalList
import com.example.goalapp.ui.theme.GoalAppTheme

@Composable
fun HomeScreen(
    onFABClick: () -> Unit = {},
    onGoalClick: (Int) -> Unit = {}
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "Goals")
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onFABClick) {
                Icon(Icons.Default.Add, contentDescription = "Add new goal")
            }
        }
    ) { contentPadding ->
        GoalList(
            modifier = Modifier.padding(contentPadding),
            onGoalClicked = onGoalClick
        )
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    GoalAppTheme {
        HomeScreen()
    }
}