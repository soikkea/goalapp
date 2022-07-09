package com.example.goalapp.ui.goallist

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.goalapp.ui.theme.GoalAppTheme

@Composable
fun GoalList(
    modifier: Modifier = Modifier,
    onGoalClicked: (Int) -> Unit = {}
) {
    val placeholderGoalIds = listOf<Int>(1, 2, 3)
    LazyColumn(modifier = modifier) {
        items(items = placeholderGoalIds) { goalId ->
            GoalListItem(
                goalId = goalId,
                onClicked = onGoalClicked
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    GoalAppTheme {
        GoalList()
    }
}