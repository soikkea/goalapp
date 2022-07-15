package com.example.goalapp.data

import androidx.room.Embedded
import androidx.room.Relation
import java.time.Duration
import java.time.LocalDate

data class GoalWithProgress(
    @Embedded val goal: Goal,
    @Relation(
        parentColumn = "id",
        entityColumn = "goalId"
    )
    val progress: List<GoalProgress> = emptyList()
) {

    fun totalProgress(): Int {
        return progress.sumOf { progress -> progress.value }
    }

    fun totalProgressBeforeDate(date: LocalDate): Int {
        return progress.filter { progress -> progress.date.isBefore(date) }
            .sumOf { progress -> progress.value }
    }

    fun progressForDay(date: LocalDate): Int? {
        return progress.find { progress -> progress.date.isEqual(date) }?.value
    }

    fun totalDays(): Long {
        return Duration.between(goal.startDate.atStartOfDay(), goal.endDate.atStartOfDay()).toDays() + 1
    }

    fun daysBeforeStart(now: LocalDate): Long {
        if (now == goal.startDate || now.isAfter(goal.startDate)) {
            return 0
        }
        return Duration.between(now.atStartOfDay(), goal.startDate.atStartOfDay()).toDays()
    }

    fun daysRemaining(now: LocalDate): Long {
        if (now == goal.endDate) {
            return 1
        }
        return Duration.between(now.atStartOfDay(), goal.endDate.atStartOfDay())
            .toDays() + if (now.isAfter(goal.endDate)) 0L else 1L
    }

    fun requiredDailyProgress(now: LocalDate): Double {
        val missingProgress = goal.target - totalProgressBeforeDate(now)
        if (missingProgress <= 0) {
            return 0.0
        }
        if (now.isAfter(goal.endDate)) {
            return missingProgress.toDouble()
        }
        val days = if (now.isBefore(goal.startDate)) totalDays() else daysRemaining(now)
        return missingProgress.toDouble() / days
    }

}