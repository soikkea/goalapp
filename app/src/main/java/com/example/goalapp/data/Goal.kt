package com.example.goalapp.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate
import java.time.LocalDateTime

@Entity(tableName = "goals")
data class Goal(
    @PrimaryKey(autoGenerate = true) val id: Long,
    var title: String,
    var startDate: LocalDate,
    var endDate: LocalDate,
    var target: Int,
    val createdAt: Long,
    var completed: Boolean = false
)