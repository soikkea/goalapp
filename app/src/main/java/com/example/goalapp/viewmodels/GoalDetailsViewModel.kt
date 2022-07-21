package com.example.goalapp.viewmodels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.goalapp.data.Goal
import com.example.goalapp.data.GoalProgress
import com.example.goalapp.data.GoalRepository
import com.example.goalapp.data.ProgressRepository
import com.example.goalapp.utilities.longToLocalDateTime
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class GoalDetailsViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val goalRepository: GoalRepository,
    private val progressRepository: ProgressRepository
) : ViewModel() {

    val goalId: Long = savedStateHandle.get<Long>(GOAL_DETAILS_ID_SAVED_STATE_KEY)!!
    val calendarDate: Long? = savedStateHandle.get<Long>(CALENDAR_INITIAL_DATE)

    val initialDate: LocalDate? = calendarDate?.let { longToLocalDateTime(it).toLocalDate() }

    val goal = goalRepository.getGoalWithProgress(goalId).asLiveData()

    companion object {
        const val GOAL_DETAILS_ID_SAVED_STATE_KEY = "goalId"
        const val CALENDAR_INITIAL_DATE = "calendarDate"
    }

    fun addProgress(date: LocalDate, value: Int) {
        goal.value?.let {
            val progress = GoalProgress(it.goal.id, date, value)
            insertProgress(progress)
        }
    }

    fun markGoalAsCompleted() {
        goal.value?.let {
            val updatedGoal = it.goal.copy(completed = true)
            updateGoal(updatedGoal)
        }
    }

    fun delete() {
        goal.value?.let {
            deleteGoal(it.goal)
        }
    }

    private fun insertProgress(progress: GoalProgress) = viewModelScope.launch {
        progressRepository.insertProgress(progress)
    }

    private fun updateGoal(goal: Goal) = viewModelScope.launch {
        if (goal.id > 0) {
            goalRepository.updateGoal(goal)
        }
    }

    private fun deleteGoal(goal: Goal) = viewModelScope.launch {
        goalRepository.deleteGoal(goal)
    }
}