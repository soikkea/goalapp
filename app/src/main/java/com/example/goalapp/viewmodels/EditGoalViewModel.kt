package com.example.goalapp.viewmodels

import android.text.TextUtils
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.goalapp.data.Goal
import com.example.goalapp.data.GoalRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class EditGoalViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val goalRepository: GoalRepository
) : ViewModel() {

    val goalId: Long? = savedStateHandle[GOAL_ID_SAVED_STATE_KEY]

    private var goal: Goal? = null

    var goalTitle by mutableStateOf("")
        private set
    var goalTarget: Int? by mutableStateOf(null)
        private set
    var startDate by mutableStateOf(LocalDate.now())
        private set
    var endDate by mutableStateOf(startDate.plusDays(1))
        private set

    init {
        if (goalId != null && goalId > 0) {
            viewModelScope.launch {
                goalRepository.getGoal(goalId).collect { value ->
                    goal = value
                    goalTitle = value.title
                    goalTarget = value.target
                    startDate = value.startDate
                    endDate = value.endDate
                }
            }
        }
    }

    fun getValidationStatus(): Boolean {
        return (!TextUtils.isEmpty(getTrimmedTitle())) && (goalTarget != null && goalTarget!! > 0) && (endDate.isAfter(
            startDate
        ))
    }

    fun getTrimmedTitle(): String {
        return goalTitle.trim()
    }

    fun onTitleChanged(title: String) {
        goalTitle = title
    }

    fun onTargetChanged(target: Int?) {
        goalTarget = target
    }

    fun onStartDateChanged(date: LocalDate) {
        startDate = date
    }

    fun onEndDateChanged(date: LocalDate) {
        endDate = date
    }

    fun saveGoal(): Boolean {
        if (!getValidationStatus()) {
            return false
        }
        val target: Int = goalTarget!!
        if (goalId != null && goalId > 0) {
            val updatedGoal = goal!!.copy(
                title = getTrimmedTitle(),
                startDate = startDate,
                endDate = endDate,
                target = target
            )
            updateGoal(updatedGoal)
        } else {
            val newGoal = Goal.create(getTrimmedTitle(), startDate, endDate, target)
            insertGoal(newGoal)
        }
        return true
    }

    private fun insertGoal(goal: Goal) = viewModelScope.launch {
        goalRepository.insertGoal(goal)
    }

    private fun updateGoal(goal: Goal) = viewModelScope.launch {
        goalRepository.updateGoal(goal)
    }

    companion object {
        const val GOAL_ID_SAVED_STATE_KEY = "goalId"
    }
}