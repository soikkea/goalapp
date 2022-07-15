package com.example.goalapp.data

import androidx.room.Embedded
import androidx.room.Relation

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

}