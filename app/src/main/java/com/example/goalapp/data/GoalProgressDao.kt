package com.example.goalapp.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

@Dao
interface GoalProgressDao {
    @Query("SELECT * FROM progress WHERE goalId = :goalId AND date = :date")
    fun getProgressForGoalAndDate(goalId: Long, date: LocalDate): Flow<GoalProgress>

    @Query("SELECT * FROM progress")
    fun getAllProgress(): Flow<List<GoalProgress>>

    @Query("SELECT * FROM progress WHERE goalId = :goalId")
    fun getProgressForGoal(goalId: Long): Flow<List<GoalProgress>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProgress(progress: GoalProgress)

    @Update
    suspend fun updateProgress(vararg progress: GoalProgress)

    @Delete
    suspend fun deleteProgress(vararg progress: GoalProgress)

    @Query("DELETE FROM progress WHERE goalId = :goalId")
    suspend fun deleteAllProgressForGoal(goalId: Long)
}