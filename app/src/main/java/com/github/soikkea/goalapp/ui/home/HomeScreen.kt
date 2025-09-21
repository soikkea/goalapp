package com.github.soikkea.goalapp.ui.home

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.github.soikkea.goalapp.R
import com.github.soikkea.goalapp.data.GoalWithProgress
import com.github.soikkea.goalapp.ui.goallist.GoalList
import com.github.soikkea.goalapp.ui.theme.GoalAppTheme
import com.github.soikkea.goalapp.viewmodels.GoalListViewModel
import java.time.LocalDate

@Composable
fun HomeScreen(
    onFABClick: () -> Unit = {},
    onGoalClick: (Long) -> Unit = {},
    onAboutClick: () -> Unit,
    snackBarHostState: SnackbarHostState = remember { SnackbarHostState() },
    viewModel: GoalListViewModel
) {
    val goals by viewModel.allGoalsWithProgress.observeAsState(emptyList())
    val today = LocalDate.now()
    HomeScreenScaffold(onFABClick, goals, today, onGoalClick, onAboutClick, snackBarHostState)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeScreenScaffold(
    onFABClick: () -> Unit,
    goals: List<GoalWithProgress>,
    date: LocalDate,
    onGoalClick: (Long) -> Unit,
    onAboutClick: () -> Unit,
    snackBarHostState: SnackbarHostState = remember { SnackbarHostState() },
) {
    Scaffold(
        snackbarHost = { SnackbarHost(snackBarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "Goals")
                },
                actions = {
                    IconButton(onClick = { onAboutClick() }) {
                        Icon(
                            Icons.Filled.Info,
                            contentDescription = stringResource(id = R.string.about)
                        )
                    }
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
            date = date,
            onGoalClicked = onGoalClick
        )
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    val date = LocalDate.now()
    GoalAppTheme {
        HomeScreenScaffold(
            {},
            emptyList(),
            date,
            {},
            {}
        )
    }
}