package com.github.soikkea.goalapp.ui

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.github.soikkea.goalapp.ui.about.AboutScreen
import com.github.soikkea.goalapp.ui.calendar.CalendarScreen
import com.github.soikkea.goalapp.ui.details.GoalDetailsScreen
import com.github.soikkea.goalapp.ui.home.HomeScreen
import com.github.soikkea.goalapp.ui.newgoal.NewGoalScreen
import com.github.soikkea.goalapp.viewmodels.EditGoalViewModel
import com.github.soikkea.goalapp.viewmodels.GoalDetailsViewModel
import com.github.soikkea.goalapp.viewmodels.GoalListViewModel

@Composable
fun GoalAppNavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    NavHost(
        navController = navController,
        startDestination = GoalScreen.Home.name,
        modifier = modifier
    ) {
        composable(GoalScreen.Home.name) {
            val listViewModel = hiltViewModel<GoalListViewModel>()
            HomeScreen(
                onFABClick = { navController.navigate(GoalScreen.NewGoal.name) },
                onGoalClick = { goalId -> navigateToGoalDetails(navController, goalId) },
                onAboutClick = { navController.navigate(GoalScreen.About.name) },
                snackBarHostState = snackbarHostState,
                viewModel = listViewModel
            )
        }
        composable(GoalScreen.NewGoal.name) {
            val editViewModel = hiltViewModel<EditGoalViewModel>()
            NewGoalScreen(
                onBack = { navController.popBackStack() },
                viewModel = editViewModel
            )
        }
        val goalDetailsName = GoalScreen.Details.name
        composable(
            route = "$goalDetailsName/{goalId}",
            arguments = listOf(
                navArgument("goalId") {
                    type = NavType.LongType
                }
            )
        ) {
            val detailsViewModel = hiltViewModel<GoalDetailsViewModel>()
            GoalDetailsScreen(
                onBack = { navController.popBackStack() },
                onEditClick = { goalId -> navigateToEditGoal(navController, goalId) },
                onGoToCalendar = { goalId, date ->
                    navigateToCalendar(
                        navController,
                        goalId,
                        date
                    )
                },
                snackBarHostState = snackbarHostState,
                scope = scope,
                viewModel = detailsViewModel
            )
        }
        composable(
            route = "${GoalScreen.EditGoal.name}/{goalId}",
            arguments = listOf(
                navArgument("goalId") {
                    type = NavType.LongType
                }
            )
        ) {
            val editViewModel = hiltViewModel<EditGoalViewModel>()
            NewGoalScreen(onBack = { navController.popBackStack() }, viewModel = editViewModel)
        }
        composable(
            route = "${GoalScreen.Calendar.name}/{goalId}/{date}",
            arguments = listOf(
                navArgument("goalId") {
                    type = NavType.LongType
                },
                navArgument("date") {
                    type = NavType.LongType
                }
            )
        ) {
            val calendarViewModel = hiltViewModel<GoalDetailsViewModel>()
            CalendarScreen(onBack = { navController.popBackStack() }, viewModel = calendarViewModel)
        }
        composable(
            GoalScreen.About.name
        ) {
            AboutScreen(
                onBack = { navController.popBackStack() }
            )
        }
    }
}

private fun navigateToGoalDetails(
    navController: NavController,
    goalId: Long
) {
    navController.navigate("${GoalScreen.Details.name}/$goalId")
}

private fun navigateToEditGoal(
    navController: NavController,
    goalId: Long
) {
    navController.navigate("${GoalScreen.EditGoal.name}/$goalId")
}

private fun navigateToCalendar(
    navController: NavController,
    goalId: Long,
    date: Long
) {
    navController.navigate("${GoalScreen.Calendar.name}/$goalId/$date")
}