package com.example.goalapp.data

import androidx.annotation.WorkerThread
import java.time.LocalDate
import javax.inject.Singleton

@Singleton
class ProgressRepository(private val progressDao: GoalProgressDao) {

    fun getProgress(goalId: Long, date: LocalDate) =
        progressDao.getProgressForGoalAndDate(goalId, date)

    @WorkerThread
    suspend fun insertProgress(progress: GoalProgress) {
        progressDao.insertProgress(progress)
    }

    @WorkerThread
    suspend fun updatePrgress(progress: GoalProgress) {
        progressDao.updateProgress(progress)
    }

    companion object {
        @Volatile
        private var instance: ProgressRepository? = null

        fun getInstance(progressDao: GoalProgressDao): ProgressRepository {
            return instance ?: synchronized(this) {
                return instance ?: ProgressRepository(progressDao).also { instance = it }
            }
        }
    }
}