package com.example.goalapp.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface GoalDao {
    @Query("SELECT * FROM goals WHERE completed = 0 ORDER BY endDate ASC, createdAt ASC")
    fun getOrderedGoals(): Flow<List<Goal>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertGoal(goal: Goal): Long

    @Update
    suspend fun updateGoals(vararg goals: Goal)

    @Delete
    suspend fun deleteGoals(vararg goals: Goal)

    @Query("SELECT * FROM goals WHERE id = :goalId")
    fun getGoal(goalId: Long): Flow<Goal>

    @Transaction
    @Query("SELECT * FROM goals WHERE completed = 0 ORDER BY endDate ASC, createdAt ASC")
    fun getGoalsWithProgress(): Flow<List<GoalWithProgress>>

    @Transaction
    @Query("SELECT * FROM goals WHERE id = :goalId")
    fun getGoalWithProgress(goalId: Long): Flow<GoalWithProgress>
}