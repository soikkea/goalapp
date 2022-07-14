package com.example.goalapp.ui.home

import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.goalapp.data.GoalWithProgress
import com.example.goalapp.ui.goallist.GoalList
import com.example.goalapp.ui.theme.GoalAppTheme
import com.example.goalapp.viewmodels.GoalListViewModel

@Composable
fun HomeScreen(
    onFABClick: () -> Unit = {},
    onGoalClick: (Long) -> Unit = {},
    viewModel: GoalListViewModel
) {
    val goals by viewModel.allGoalsWithProgress.observeAsState(emptyList())
    HomeScreenScaffold(onFABClick, goals, onGoalClick)
}

@Composable
private fun HomeScreenScaffold(
    onFABClick: () -> Unit,
    goals: List<GoalWithProgress>,
    onGoalClick: (Long) -> Unit
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
            list = goals,
            onGoalClicked = onGoalClick
        )
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    GoalAppTheme {
        HomeScreenScaffold(
            {},
            emptyList(),
            {}
        )
    }
}