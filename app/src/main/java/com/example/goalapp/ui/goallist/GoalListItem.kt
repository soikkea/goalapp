package com.example.goalapp.ui.goallist

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.goalapp.data.Goal
import com.example.goalapp.data.GoalProgress
import com.example.goalapp.data.GoalWithProgress
import com.example.goalapp.ui.theme.GoalAppTheme
import com.example.goalapp.ui.utilities.dueInText
import java.time.LocalDate

@Composable
fun GoalListItem(
    goal: GoalWithProgress,
    date: LocalDate,
    onClicked: (Long) -> Unit = {}
) {
    val resources = LocalContext.current.resources
    val progress = goal.totalProgress().toFloat() / goal.goal.target
    val expectedProgress = goal.expectedProgressForDay(date).toFloat() / goal.goal.target
    Card(modifier = Modifier
        .fillMaxWidth()
        .clickable { onClicked(goal.goal.id) }) {
        Row(modifier = Modifier.padding(vertical = 4.dp).height(IntrinsicSize.Max)) {
            if (goal.lastUpdated() == date) {
                Column() {
                Spacer(modifier = Modifier
                    .width(8.dp)
                    .fillMaxHeight()
                    .background(color = Color.Green))
                }
            }
            Column() {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column() {
                        Text(text = goal.goal.title, style = MaterialTheme.typography.h6)
                        Text(text = dueInText(goal.goal, date, resources), style = MaterialTheme.typography.subtitle1)
                    }
                    Text(text = "${String.format("%.2f", goal.completionPercentage())}%", style = MaterialTheme.typography.h4)
                }
                Box(modifier = Modifier
                    .fillMaxWidth()
                    .height(16.dp)
                    .padding(horizontal = 4.dp)) {
                    ProgressBar(progress = progress, expected = expectedProgress)
                }
            }
        }
    }
}

@Composable
fun ProgressBar(
    progress: Float,
    expected: Float
) {
    // TODO choose correct color
    val color = Color.Green
    val backgroundColor =
        Color.Gray.copy(alpha = ProgressIndicatorDefaults.IndicatorBackgroundOpacity)
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.CenterStart) {
        LinearProgressIndicator(
            progress = expected,
            modifier = Modifier.fillMaxWidth(),
            color = color.copy(alpha = ProgressIndicatorDefaults.IndicatorBackgroundOpacity),
            backgroundColor
        )
        LinearProgressIndicator(
            progress = progress,
            modifier = Modifier.fillMaxWidth(),
            color = color,
            backgroundColor = Color.Transparent
        )
    }
}

@Preview(showBackground = true, widthDp = 300, heightDp = 30)
@Composable
fun ProgressBarPreview() {
    GoalAppTheme() {
        ProgressBar(progress = 0.25f, expected = 0.5f)
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewGoalListItem() {
    val date = LocalDate.now()
    val goal = Goal.create("Test Goal", date, date.plusDays(7), 10)
    val gwp = GoalWithProgress(goal, emptyList())
    GoalAppTheme {
        GoalListItem(goal = gwp, date = date)
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewGoalListItemLastUpdatedToday() {
    val date = LocalDate.now()
    val goal = Goal.create("Test Goal", date, date.plusDays(7), 10)
    val progress = GoalProgress(goal.id, date, 2)
    val gwp = GoalWithProgress(goal, listOf(progress))
    GoalAppTheme {
        GoalListItem(goal = gwp, date = date)
    }
}