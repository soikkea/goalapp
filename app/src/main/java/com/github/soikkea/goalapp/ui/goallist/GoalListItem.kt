package com.github.soikkea.goalapp.ui.goallist

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.soikkea.goalapp.data.Goal
import com.github.soikkea.goalapp.data.GoalProgress
import com.github.soikkea.goalapp.data.GoalWithProgress
import com.github.soikkea.goalapp.ui.theme.GoalAppTheme
import com.github.soikkea.goalapp.ui.utilities.dueInText
import com.github.soikkea.goalapp.ui.utilities.getProgressStatusColor
import java.time.LocalDate
import androidx.compose.ui.platform.LocalResources
import java.util.Locale

@Composable
fun GoalListItem(
    goal: GoalWithProgress,
    date: LocalDate,
    onClicked: (Long) -> Unit = {}
) {
    val resources = LocalResources.current
    val progress = goal.totalProgress().toFloat() / goal.goal.target
    val expectedProgress = goal.expectedProgressForDay(date).toFloat() / goal.goal.target
    Card(modifier = Modifier
        .fillMaxWidth()
        .clickable { onClicked(goal.goal.id) }) {
        Row(
            modifier = Modifier
                .padding(vertical = 4.dp)
                .height(IntrinsicSize.Max)
        ) {
            if (goal.lastUpdated() == date) {
                Column {
                    Spacer(
                        modifier = Modifier
                            .width(8.dp)
                            .fillMaxHeight()
                            .background(color = Color.Green)
                    )
                }
            }
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(
                        modifier = Modifier.weight(1f, fill = true)
                    ) {
                        Text(text = goal.goal.title, style = MaterialTheme.typography.titleLarge)
                        Text(
                            text = dueInText(goal.goal, date, resources),
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                    Text(
                        text = "${String.format(Locale.getDefault(),"%.1f", goal.completionPercentage())}%",
                        style = MaterialTheme.typography.headlineMedium,
                        textAlign = TextAlign.Right,
                        maxLines = 1
                    )
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(16.dp)
                        .padding(horizontal = 4.dp)
                ) {
                    ProgressBar(
                        progress = progress,
                        expected = expectedProgress,
                        getProgressStatusColor(goal.getProgressStatus(date))
                    )
                }
            }
        }
    }
}

private const val INDICATOR_BACKGROUND_OPACITY = 0.24f

@Composable
fun ProgressBar(
    progress: Float,
    expected: Float,
    color: Color
) {
    val backgroundColor =
        Color.Gray.copy(alpha = INDICATOR_BACKGROUND_OPACITY)
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.BottomStart) {
        LinearProgressIndicator(
            progress = {expected},
            modifier = Modifier
                .fillMaxWidth()
                .height(10.dp),
            color = color.copy(alpha = INDICATOR_BACKGROUND_OPACITY),
            backgroundColor,
            drawStopIndicator = {},
            gapSize = 0.dp,
        )
        LinearProgressIndicator(
            progress = {progress},
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp),
            color = color,
            trackColor = Color.Transparent,
            drawStopIndicator = {}
        )
    }
}

@Preview(showBackground = true, widthDp = 300, heightDp = 30)
@Composable
fun ProgressBarPreview() {
    GoalAppTheme {
        ProgressBar(progress = 0.25f, expected = 0.5f, color = Color.Green)
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

@Preview(showBackground = true)
@Composable
fun PreviewGoalListItemLongName() {
    val date = LocalDate.now()
    val goal = Goal.create("Test GoalAAAAAAAAAAAAAAAAAA", date, date.plusDays(7), 10)
    val gwp = GoalWithProgress(goal, emptyList())
    GoalAppTheme {
        GoalListItem(goal = gwp, date = date)
    }
}

@Preview(showBackground = true, widthDp = 300)
@Composable
fun PreviewGoalListItemLongNameCompleted() {
    val date = LocalDate.now()
    val goal = Goal.create("Test GoalAAAAAAAAAAAAAAAAAA", date, date.plusDays(7), 10)
    val gwp = GoalWithProgress(
        goal, listOf(
            GoalProgress(goal.id, date, 10)
        )
    )
    GoalAppTheme(true) {
        GoalListItem(goal = gwp, date = date)
    }
}