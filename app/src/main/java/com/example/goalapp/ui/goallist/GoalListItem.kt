package com.example.goalapp.ui.goallist

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.goalapp.data.Goal
import com.example.goalapp.data.GoalWithProgress
import com.example.goalapp.ui.theme.GoalAppTheme
import java.time.LocalDate

@Composable
fun GoalListItem(
    goal: GoalWithProgress,
    onClicked: (Long) -> Unit = {}
) {
    Card(modifier = Modifier
        .fillMaxWidth()
        .clickable { onClicked(goal.goal.id) }) {
        Row(modifier = Modifier.padding(vertical =  4.dp)) {
            Column() {
                Row() {
                    Column() {
                        Text(text = goal.goal.title)
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewGoalListItem() {
    val date = LocalDate.now()
    val goal = Goal.create("Test Goal", date, date.plusDays(7), 10)
    val gwp = GoalWithProgress(goal, emptyList())
    GoalAppTheme {
        GoalListItem(goal = gwp)
    }
}
