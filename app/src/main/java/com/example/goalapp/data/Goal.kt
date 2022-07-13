package com.example.goalapp.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.Instant
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
) {
    companion object {
        fun create(
            title: String, startDate: LocalDate, endDate: LocalDate, target: Int
        ): Goal {
            val timestamp = Instant.now().toEpochMilli()
            return Goal(
                0, title, startDate, endDate, target, timestamp, false
            )
        }
    }
}