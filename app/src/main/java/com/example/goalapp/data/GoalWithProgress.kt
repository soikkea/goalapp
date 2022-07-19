package com.example.goalapp.data

import androidx.room.Embedded
import androidx.room.Relation
import java.time.Duration
import java.time.LocalDate
import kotlin.math.ceil

private const val PROGRESS_MARGIN = 0.1

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

    fun completionPercentage(): Double {
        return totalProgress().toDouble() / goal.target.toDouble() * 100.0
    }

    fun totalProgressBeforeDate(date: LocalDate): Int {
        return progress.filter { progress -> progress.date.isBefore(date) }
            .sumOf { progress -> progress.value }
    }

    fun progressForDay(date: LocalDate): Int? {
        return progress.find { progress -> progress.date.isEqual(date) }?.value
    }

    fun totalDays(): Long {
        return Duration.between(goal.startDate.atStartOfDay(), goal.endDate.atStartOfDay())
            .toDays() + 1
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

    fun expectedProgressForDay(now: LocalDate): Int {
        if (now.isAfter(goal.endDate)) {
            return goal.target
        }
        if (now.isBefore(goal.startDate)) {
            return 0
        }
        val expectedDaily = goal.target.toDouble() / totalDays().toDouble()
        val daysPassed =
            Duration.between(goal.startDate.atStartOfDay(), now.atStartOfDay()).toDays() + 1
        return ceil(expectedDaily * daysPassed).toInt()
    }

    fun isCompleted(): Boolean {
        return totalProgress() >= goal.target
    }

    fun lastUpdated(): LocalDate? {
        return progress.maxOfOrNull { p -> p.date }
    }

    fun getTimeStatus(now: LocalDate): TimeStatus {
        if (now.isBefore(goal.startDate)) {
            return TimeStatus.NOT_STARTED
        }
        if (now.isAfter(goal.endDate)) {
            return TimeStatus.OVERDUE
        }
        return TimeStatus.ONGOING
    }

    fun getProgressStatus(now: LocalDate): ProgressStatus {
        val timeStatus = getTimeStatus(now)
        if (timeStatus == TimeStatus.NOT_STARTED) {
            return if (totalProgress() > 0) {
                ProgressStatus.EARLY
            } else {
                ProgressStatus.ON_TIME
            }
        }
        if (timeStatus == TimeStatus.OVERDUE) {
            return if (isCompleted()) {
                ProgressStatus.EARLY
            } else {
                ProgressStatus.LATE
            }
        }
        if (now.isEqual(goal.startDate) && progress.isEmpty()) {
            return ProgressStatus.ON_TIME
        }

        val normalizedProgress = totalProgressBeforeDate(now.plusDays(1)).toDouble() / goal.target
        // If goal has not been updated since yesterday, expect that it might still be updated today,
        // instead of marking it late
        val compareToDate =
            if (now.minusDays(1) == lastUpdated()) lastUpdated()!!.atStartOfDay() else now.atStartOfDay()
        val normalizedDays = (Duration.between(goal.startDate.atStartOfDay(), compareToDate)
            .toDays() + 1).toDouble() / totalDays()

        val normalizedFactor = normalizedProgress / normalizedDays

        if (normalizedFactor > (1 + PROGRESS_MARGIN)) {
            return ProgressStatus.EARLY
        }
        if (normalizedFactor < (1 - PROGRESS_MARGIN)) {
            return ProgressStatus.LATE
        }

        return ProgressStatus.ON_TIME
    }

}

enum class TimeStatus {
    NOT_STARTED,
    ONGOING,
    OVERDUE;
}

enum class ProgressStatus {
    ON_TIME,
    LATE,
    EARLY;
}