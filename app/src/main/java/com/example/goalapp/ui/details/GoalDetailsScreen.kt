package com.example.goalapp.ui.details

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.goalapp.R
import com.example.goalapp.data.Goal
import com.example.goalapp.data.GoalWithProgress
import com.example.goalapp.ui.components.TopAppBarWithBackButton
import com.example.goalapp.ui.theme.GoalAppTheme
import com.example.goalapp.viewmodels.GoalDetailsViewModel
import java.time.LocalDate

@Composable
fun GoalDetailsScreen(
    onBack: () -> Unit,
    viewModel: GoalDetailsViewModel
) {
    var openDialog by remember {
        mutableStateOf(false)
    }
    val today = LocalDate.now()
    val goal by viewModel.goal.observeAsState()
    GoalDetailsScaffold(onBack, { openDialog = true }, goal, today)
    if (openDialog) {
        // TODO
        //AddProgressDialog(onClose = { openDialog.value = false })
    }
}

@Composable
private fun GoalDetailsScaffold(
    onBack: () -> Unit,
    onFABClick: () -> Unit,
    goal: GoalWithProgress?,
    currentDate: LocalDate
) {
    Scaffold(
        topBar = {
            TopAppBarWithBackButton(
                title = stringResource(id = R.string.details),
                onBack = onBack
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
            GoalDetailContent(contentPadding, goal)
        }
    }
}

@Composable
private fun GoalDetailContent(
    contentPadding: PaddingValues,
    goal: GoalWithProgress
) {
    Column(modifier = Modifier.padding(contentPadding)) {
        Text(text = goal.goal.title)
        Row() {
            Text(text = stringResource(id = R.string.start_date))
            Text(text = goal.goal.startDate.toString())
        }
        Row() {
            Text(text = stringResource(id = R.string.end_date))
            Text(text = goal.goal.endDate.toString())
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
            goal = gwp,
            currentDate = today
        )
    }
}