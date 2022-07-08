package com.example.goalapp.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.goalapp.ui.home.HomeScreen
import com.example.goalapp.ui.newgoal.NewGoalScreen

@Composable
fun GoalAppNavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(navController = navController, startDestination = GoalScreen.Home.name, modifier = modifier) {
        composable(GoalScreen.Home.name) {
            HomeScreen(
                onFABClick = {navController.navigate(GoalScreen.NewGoal.name)}
            )
        }
        composable(GoalScreen.NewGoal.name) {
            NewGoalScreen (
                onBack = {navController.popBackStack()}
            )
        }
        // TODO
    }
}