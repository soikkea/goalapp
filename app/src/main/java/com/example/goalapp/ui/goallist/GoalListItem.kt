package com.example.goalapp.ui.goallist

import android.content.res.Resources
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Card
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.ProgressIndicatorDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.goalapp.R
import com.example.goalapp.data.Goal
import com.example.goalapp.data.GoalWithProgress
import com.example.goalapp.ui.theme.GoalAppTheme
import java.time.Duration
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
        Row(modifier = Modifier.padding(vertical = 4.dp)) {
            // TODO: Updated today status
            Column() {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column() {
                        Text(text = goal.goal.title)
                        Text(text = dueInText(goal.goal, date, resources))
                    }
                    Text(text = "${String.format("%.2f", goal.completionPercentage())}%")
                }
                Box(modifier = Modifier.fillMaxWidth().height(16.dp).padding(horizontal = 4.dp)) {
                    ProgressBar(progress = progress, expected = expectedProgress)
                }
            }
        }
    }
}

private fun dueInText(goal: Goal, date: LocalDate, resources: Resources): String {
    val hasStarted = !(date.isBefore(goal.startDate))
    val overdue = date.isAfter(goal.endDate)
    val today = date.atStartOfDay()
    val startDate = goal.startDate.atStartOfDay()
    if (!hasStarted) {
        val startsInDays = Duration.between(today, startDate).toDays()
        return resources.getQuantityString(
            R.plurals.starts_in_days,
            startsInDays.toInt(), startsInDays
        )
    }
    val endDate = goal.endDate.atStartOfDay()
    if (overdue) {
        val daysOverdue = Duration.between(endDate, today).toDays()
        return resources.getQuantityString(R.plurals.overdue_days, daysOverdue.toInt(), daysOverdue)
    }
    val daysRemaining = Duration.between(today, endDate).toDays() + 1
    return resources.getQuantityString(
        R.plurals.days_remaining,
        daysRemaining.toInt(), daysRemaining
    )
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
