package com.example.goalapp.data

import androidx.annotation.WorkerThread
import kotlinx.coroutines.flow.Flow
import javax.inject.Singleton

@Singleton
class GoalRepository(private val goalDao: GoalDao) {

    val allGoals: Flow<List<Goal>> = goalDao.getOrderedGoals()
    val allGoalsWithProgress: Flow<List<GoalWithProgress>> = goalDao.getGoalsWithProgress()

    @WorkerThread
    suspend fun insertGoal(goal: Goal) {
        goalDao.insertGoal(goal)
    }

    fun getGoal(goalId: Long) = goalDao.getGoal(goalId)

    fun getGoalWithProgress(goalId: Long) = goalDao.getGoalWithProgress(goalId)

    @WorkerThread
    suspend fun updateGoal(goal: Goal) {
        goalDao.updateGoals(goal)
    }

    @WorkerThread
    suspend fun deleteGoal(goal: Goal) {
        goalDao.deleteGoals(goal)
    }

    companion object {
        @Volatile
        private var instance: GoalRepository? = null

        fun getInstance(goalDao: GoalDao) =
            instance ?: synchronized(this) {
                instance ?: GoalRepository(goalDao).also { instance = it }
            }
    }
}