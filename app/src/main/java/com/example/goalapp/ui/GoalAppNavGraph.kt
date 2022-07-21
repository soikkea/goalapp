package com.example.goalapp.ui

import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.goalapp.ui.calendar.CalendarScreen
import com.example.goalapp.ui.details.GoalDetailsScreen
import com.example.goalapp.ui.home.HomeScreen
import com.example.goalapp.ui.newgoal.NewGoalScreen
import com.example.goalapp.viewmodels.EditGoalViewModel
import com.example.goalapp.viewmodels.GoalDetailsViewModel
import com.example.goalapp.viewmodels.GoalListViewModel

@Composable
fun GoalAppNavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    val scaffoldState = rememberScaffoldState()
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
                scaffoldState = scaffoldState,
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
        ) { entry ->
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
                scaffoldState = scaffoldState,
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
        ) { entry ->
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
        ) { entry ->
            val calendarViewModel = hiltViewModel<GoalDetailsViewModel>()
            CalendarScreen(onBack = { navController.popBackStack() }, viewModel = calendarViewModel)
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