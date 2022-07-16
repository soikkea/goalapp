package com.example.goalapp.ui.details

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.goalapp.R
import com.example.goalapp.data.Goal
import com.example.goalapp.data.GoalWithProgress
import com.example.goalapp.ui.components.TopAppBarWithBackButton
import com.example.goalapp.ui.theme.GoalAppTheme
import com.example.goalapp.ui.utilities.dueInText
import com.example.goalapp.viewmodels.GoalDetailsViewModel
import java.time.LocalDate

@Composable
fun GoalDetailsScreen(
    onBack: () -> Unit,
    onEditClick: (Long) -> Unit,
    viewModel: GoalDetailsViewModel
) {
    var openDialog by remember {
        mutableStateOf(false)
    }
    var openConfirmDeleteDialog by remember {
        mutableStateOf(false)
    }
    val today = LocalDate.now()
    val goal by viewModel.goal.observeAsState()
    GoalDetailsScaffold(onBack, { openDialog = true }, onEditClick, {
        openConfirmDeleteDialog = true
    }, goal, today)
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
}

@Composable
private fun GoalDetailsScaffold(
    onBack: () -> Unit,
    onFABClick: () -> Unit,
    onEditClick: (Long) -> Unit,
    onDeleteClick: (Long) -> Unit,
    goal: GoalWithProgress?,
    currentDate: LocalDate
) {
    Scaffold(
        topBar = {
            TopAppBarWithBackButton(
                title = stringResource(id = R.string.details),
                onBack = onBack,
                actions = {
                    IconButton(onClick = { onEditClick(goal!!.goal.id) }) {
                        Icon(
                            Icons.Filled.Edit,
                            contentDescription = stringResource(id = R.string.edit)
                        )
                    }
                    IconButton(onClick = { onDeleteClick(goal!!.goal.id) }) {
                        Icon(
                            Icons.Filled.Delete,
                            contentDescription = stringResource(id = R.string.delete_goal)
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onFABClick) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = stringResource(id = R.string.add_progress)
                )
            }
        }
    ) { contentPadding ->
        if (goal != null) {
            GoalDetailContent(contentPadding, goal, currentDate)
        }
    }
}

@Composable
private fun GoalDetailContent(
    contentPadding: PaddingValues,
    goal: GoalWithProgress,
    date: LocalDate
) {
    val totalProgress = goal.totalProgress()
    val resources = LocalContext.current.resources
    Column(modifier = Modifier.padding(contentPadding)) {
        Text(text = goal.goal.title)
        Row(
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                modifier = Modifier.weight(1f),
                text = stringResource(id = R.string.start_date)
            )
            Text(modifier = Modifier.weight(1f), text = goal.goal.startDate.toString())
        }
        Row() {
            Text(modifier = Modifier.weight(1f), text = stringResource(id = R.string.end_date))
            Text(modifier = Modifier.weight(1f), text = goal.goal.endDate.toString())
        }
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = dueInText(goal.goal, date, resources))
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = stringResource(id = R.string.progress_today))
                Text(
                    text = "${goal.progressForDay(date) ?: 0}/${
                        String.format(
                            "%.2f",
                            goal.requiredDailyProgress(date)
                        )
                    }"
                )
            }
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = stringResource(id = R.string.remaining))
                Text(text = "${goal.goal.target - totalProgress}")
            }
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = stringResource(id = R.string.total_progress))
                Text(text = "${totalProgress}/${goal.goal.target}")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    val today = LocalDate.of(2022, 7, 1)
    val goal = Goal.create("Test Goal", today, today.plusDays(6), 7)
    val gwp = GoalWithProgress(goal, emptyList())
    GoalAppTheme {
        GoalDetailsScaffold(
            onBack = { /*TODO*/ },
            onFABClick = { /*TODO*/ },
            onEditClick = {},
            onDeleteClick = {},
            goal = gwp,
            currentDate = today
        )
    }
}