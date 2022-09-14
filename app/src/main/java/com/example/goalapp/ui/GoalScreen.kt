package com.example.goalapp.ui

enum class GoalScreen {
    Home,
    NewGoal,
    EditGoal,
    Details,
    Calendar,
    About;

    companion object {
        fun fromRoute(route: String?): GoalScreen =
            when (route?.substringBefore("/")) {
                Home.name -> Home
                NewGoal.name -> NewGoal
                EditGoal.name -> EditGoal
                Details.name -> Details
                Calendar.name -> Calendar
                About.name -> About
                null -> Home
                else -> throw IllegalArgumentException("Route $route is not recognized.")
            }
    }
}