package com.example.goalapp.ui

enum class GoalScreen {
    Home,
    NewGoal,
    Details,
    Calendar;

    companion object {
        fun fromRoute(route: String?): GoalScreen =
            when (route?.substringBefore("/")) {
                Home.name -> Home
                NewGoal.name -> NewGoal
                Details.name -> Details
                Calendar.name -> Calendar
                null -> Home
                else -> throw IllegalArgumentException("Route $route is not recognized.")
            }
    }
}