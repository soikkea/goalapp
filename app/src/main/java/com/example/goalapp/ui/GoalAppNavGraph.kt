package com.example.goalapp.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.goalapp.ui.details.GoalDetailsScreen
import com.example.goalapp.ui.home.HomeScreen
import com.example.goalapp.ui.newgoal.NewGoalScreen
import com.example.goalapp.viewmodels.EditGoalViewModel
import com.example.goalapp.viewmodels.NavigationViewModel

@Composable
fun GoalAppNavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    viewModel: NavigationViewModel = viewModel()
) {
    NavHost(
        navController = navController,
        startDestination = GoalScreen.Home.name,
        modifier = modifier
    ) {
        composable(GoalScreen.Home.name) {
            HomeScreen(
                onFABClick = { navController.navigate(GoalScreen.NewGoal.name) },
                onGoalClick = { goalId -> navigateToGoalDetails(navController, goalId) }
            )
        }
        composable(GoalScreen.NewGoal.name) {
            viewModel.setGoalDetailsGoalId(0L)
            val viewModel = hiltViewModel<EditGoalViewModel>()
            NewGoalScreen(
                onBack = { navController.popBackStack() },
                viewModel = viewModel
            )
        }
        val goalDetailsName = GoalScreen.Details.name
        composable(
            route = "$goalDetailsName/{goalId}",
            arguments = listOf(
                navArgument("goalId") {
                    type = NavType.IntType
                }
            )
        ) { entry ->
            val goalId = entry.arguments?.getInt("goalId")
            GoalDetailsScreen(
                goalId = goalId!!,
                onBack = { navController.popBackStack() })
        }
        // TODO
    }
}

private fun navigateToGoalDetails(
    navController: NavController,
    goalId: Int
) {
    navController.navigate("${GoalScreen.Details.name}/$goalId")
}