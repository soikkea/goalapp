package com.example.goalapp.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.*
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
                    goalTitle = value.title
                }
            }
        }
    }

    fun onTitleChanged(title: String) {
        goalTitle = title
    }

    companion object {
        const val GOAL_ID_SAVED_STATE_KEY = "goalId"
    }
}