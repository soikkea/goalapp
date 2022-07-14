package com.example.goalapp.ui.goallist

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.goalapp.data.GoalWithProgress

@Composable
fun GoalList(
    modifier: Modifier = Modifier,
    list: List<GoalWithProgress>,
    onGoalClicked: (Long) -> Unit = {}
) {
    LazyColumn(modifier = modifier) {
        items(items = list,
            key = { goalWP ->
                goalWP.goal.id
            }
        ) { goal ->
            GoalListItem(
                goal = goal,
                onClicked = onGoalClicked
            )
        }
    }
}