package com.example.goalapp.ui.utilities

import android.content.res.Resources
import com.example.goalapp.R
import com.example.goalapp.data.Goal
import java.time.Duration
import java.time.LocalDate

fun dueInText(goal: Goal, date: LocalDate, resources: Resources): String {
    val hasStarted = !(date.isBefore(goal.startDate))
    val overdue = date.isAfter(goal.endDate)
    val today = date.atStartOfDay()
    val startDate = goal.startDate.atStartOfDay()
    if (!hasStarted) {
        val startsInDays = Duration.between(today, startDate).toDays()
        return resources.getQuantityString(
            R.plurals.starts_in_days,
            startsInDays.toInt(), startsInDays
        )
    }
    val endDate = goal.endDate.atStartOfDay()
    if (overdue) {
        val daysOverdue = Duration.between(endDate, today).toDays()
        return resources.getQuantityString(R.plurals.overdue_days, daysOverdue.toInt(), daysOverdue)
    }
    val daysRemaining = Duration.between(today, endDate).toDays() + 1
    return resources.getQuantityString(
        R.plurals.days_remaining,
        daysRemaining.toInt(), daysRemaining
    )
}