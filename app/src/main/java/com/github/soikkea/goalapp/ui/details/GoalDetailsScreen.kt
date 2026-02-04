package com.github.soikkea.goalapp.ui.details

import androidx.compose.foundation.layout.*
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.soikkea.goalapp.R
import com.github.soikkea.goalapp.data.Goal
import com.github.soikkea.goalapp.data.GoalProgress
import com.github.soikkea.goalapp.data.GoalWithProgress
import com.github.soikkea.goalapp.ui.components.AnimatedCircle
import com.github.soikkea.goalapp.ui.components.TopAppBarWithBackButton
import com.github.soikkea.goalapp.ui.theme.GoalAppTheme
import com.github.soikkea.goalapp.ui.utilities.dueInText
import com.github.soikkea.goalapp.ui.utilities.getProgressStatusColor
import com.github.soikkea.goalapp.utilities.localDateTimeToLong
import com.github.soikkea.goalapp.viewmodels.GoalDetailsViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.time.LocalDate

@Composable
fun GoalDetailsScreen(
    onBack: () -> Unit,
    onEditClick: (Long) -> Unit,
    onGoToCalendar: (Long, Long) -> Unit,
    snackBarHostState: SnackbarHostState = remember { SnackbarHostState() },
    scope: CoroutineScope = rememberCoroutineScope(),
    viewModel: GoalDetailsViewModel
) {
    var openDialog by remember {
        mutableStateOf(false)
    }
    var openConfirmDeleteDialog by remember {
        mutableStateOf(false)
    }
    var openConfirmMarkCompletedDialog by remember {
        mutableStateOf(false)
    }
    val today = LocalDate.now()
    val goal by viewModel.goal.observeAsState()
    GoalDetailsScaffold(onBack, { openDialog = true }, onEditClick, {
        openConfirmDeleteDialog = true
    }, goal, today,
        onMarkCompleted = { openConfirmMarkCompletedDialog = true },
        onGoToCalendar = onGoToCalendar,
        snackBarHostState = snackBarHostState
    )
    if (goal != null && openDialog) {
        AddProgressDialog(
            goalTitle = goal!!.goal.title,
            date = today,
            dailyTarget = goal!!.requiredDailyProgress(today),
            oldProgressValue = goal!!.progressForDay(today),
            onProgressValueSaved = { value -> viewModel.addProgress(today, value) },
            targetProgress = goal!!.goal.target,
            totalProgress = goal!!.totalProgress(),
            onClose = { openDialog = false })
    }
    if (openConfirmDeleteDialog) {
        AlertDialog(onDismissRequest = { openConfirmDeleteDialog = false },
            title = { Text(text = stringResource(id = R.string.delete_goal)) },
            text = {
                Text(text = stringResource(id = R.string.confirm_delete))
            },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.delete()
                    onBack()
                }) {
                    Text(text = stringResource(id = R.string.delete))
                }
            },
            dismissButton = {
                TextButton(onClick = { openConfirmDeleteDialog = false }) {
                    Text(text = stringResource(id = R.string.cancel))
                }
            })
    }
    if (openConfirmMarkCompletedDialog) {
        AlertDialog(onDismissRequest = { openConfirmMarkCompletedDialog = false },
            title = { Text(text = stringResource(id = R.string.mark_completed)) },
            text = {
                Text(text = stringResource(id = R.string.are_you_sure))
            },
            confirmButton = {
                val snackBarText = stringResource(id = R.string.marked_completed)
                TextButton(onClick = {
                    viewModel.markGoalAsCompleted()
                    scope.launch {
                        snackBarHostState.showSnackbar(snackBarText)
                    }
                    onBack()
                }) {
                    Text(text = stringResource(id = R.string.yes))
                }
            },
            dismissButton = {
                TextButton(onClick = { openConfirmMarkCompletedDialog = false }) {
                    Text(text = stringResource(id = R.string.cancel))
                }
            })
    }
}

@Composable
private fun GoalDetailsScaffold(
    onBack: () -> Unit,
    onFABClick: () -> Unit,
    onEditClick: (Long) -> Unit,
    onDeleteClick: (Long) -> Unit,
    goal: GoalWithProgress?,
    currentDate: LocalDate,
    onMarkCompleted: (Long) -> Unit,
    onGoToCalendar: (Long, Long) -> Unit,
    snackBarHostState: SnackbarHostState = remember { SnackbarHostState() },
) {
    Scaffold(
        snackbarHost = { SnackbarHost(snackBarHostState) },
        topBar = {
            TopAppBarWithBackButton(
                title = stringResource(id = R.string.details),
                onBack = onBack,
                actions = {
                    IconButton(onClick = { onEditClick(goal!!.goal.id) }) {
                        Icon(
                            painterResource(R.drawable.edit_24px),
                            contentDescription = stringResource(id = R.string.edit)
                        )
                    }
                    IconButton(onClick = { onDeleteClick(goal!!.goal.id) }) {
                        Icon(
                            painterResource(R.drawable.delete_24px),
                            contentDescription = stringResource(id = R.string.delete_goal)
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onFABClick) {
                Icon(
                    painterResource(R.drawable.add_24px),
                    contentDescription = stringResource(id = R.string.add_progress)
                )
            }
        }
    ) { contentPadding ->
        if (goal != null) {
            GoalDetailContent(contentPadding, goal, currentDate, onMarkCompleted, onGoToCalendar)
        }
    }
}

@Composable
private fun GoalDetailContent(
    contentPadding: PaddingValues,
    goal: GoalWithProgress,
    date: LocalDate,
    onMarkCompleted: (Long) -> Unit,
    onGoToCalendar: (Long, Long) -> Unit
) {
    val totalProgress = goal.totalProgress()
    val resources = LocalContext.current.resources
    Column(modifier = Modifier.padding(contentPadding)) {
        Text(text = goal.goal.title, style = MaterialTheme.typography.headlineMedium)
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                modifier = Modifier.weight(1f),
                text = stringResource(id = R.string.start_date),
                textAlign = TextAlign.Right
            )
            Spacer(modifier = Modifier.weight(0.05f))
            Text(
                modifier = Modifier.weight(1f),
                text = goal.goal.startDate.toString()
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                modifier = Modifier.weight(1f), text = stringResource(id = R.string.end_date),
                textAlign = TextAlign.Right
            )
            Spacer(modifier = Modifier.weight(0.05f))
            Text(modifier = Modifier.weight(1f), text = goal.goal.endDate.toString())
        }
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = dueInText(goal.goal, date, resources))
            Button(onClick = {
                onGoToCalendar(
                    goal.goal.id,
                    localDateTimeToLong(date.atStartOfDay())
                )
            }) {
                Text(text = stringResource(id = R.string.calendar))
            }
        }
        val totalProgressNormalized = goal.totalProgress().toFloat() / goal.goal.target
        val expectedProgressNormalized =
            goal.expectedProgressForDay(date).toFloat() / goal.goal.target
        val progressColor = getProgressStatusColor(goal.getProgressStatus(date))
        Box {
            AnimatedCircle(
                progress = totalProgressNormalized,
                expected = expectedProgressNormalized,
                color = progressColor,
                modifier = Modifier
                    .height(300.dp)
                    .fillMaxWidth()
                    .align(
                        Alignment.Center
                    )
            )
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.align(Alignment.Center)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = stringResource(id = R.string.progress_today),
                        style = MaterialTheme.typography.headlineSmall
                    )
                    Text(
                        text = "${goal.progressForDay(date) ?: 0}/${
                            String.format(
                                "%.2f",
                                goal.requiredDailyProgress(date)
                            )
                        }",
                        style = MaterialTheme.typography.headlineSmall
                    )
                }
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = stringResource(id = R.string.total_progress),
                        style = MaterialTheme.typography.titleLarge
                    )
                    Text(
                        text = "${totalProgress}/${goal.goal.target}",
                        style = MaterialTheme.typography.titleLarge
                    )
                }
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = stringResource(id = R.string.remaining))
                    Text(text = "${goal.goal.target - totalProgress}")
                }
            }
        }
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (goal.isCompleted()) {
                Button(onClick = { onMarkCompleted(goal.goal.id) }) {
                    Icon(
                        painterResource(R.drawable.check_24px),
                        contentDescription = null,
                        modifier = Modifier.size(ButtonDefaults.IconSize)
                    )
                    Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                    Text(stringResource(id = R.string.mark_completed))
                }
            }
        }
        ProgressChart()
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    val today = LocalDate.of(2022, 7, 1)
    val goal = Goal.create("Test Goal", today, today.plusDays(6), 7)
    val gwp = GoalWithProgress(goal, emptyList())
    GoalAppTheme(darkTheme = true) {
        GoalDetailsScaffold(
            onBack = {},
            onFABClick = {},
            onEditClick = {},
            onDeleteClick = {},
            goal = gwp,
            currentDate = today,
            onMarkCompleted = {},
            onGoToCalendar = { _, _ -> }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun CompletedPreview() {
    val today = LocalDate.of(2022, 7, 1)
    val goal = Goal.create("Test Goal", today, today.plusDays(6), 1)
    val gwp = GoalWithProgress(
        goal, listOf(
            GoalProgress(0L, today, 1)
        )
    )
    GoalAppTheme(darkTheme = true) {
        GoalDetailsScaffold(
            onBack = {},
            onFABClick = {},
            onEditClick = {},
            onDeleteClick = {},
            goal = gwp,
            currentDate = today,
            onMarkCompleted = {},
            onGoToCalendar = { _, _ -> }
        )
    }
}