package com.example.goalapp.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE
import java.time.LocalDate

@Entity(
    tableName = "progress",
    primaryKeys = ["goalId", "date"],
    foreignKeys = [ForeignKey(
        onDelete = CASCADE,
        entity = Goal::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("goalId")
    )]
)
data class GoalProgress(val goalId: Int, val date: LocalDate, var value: Int)