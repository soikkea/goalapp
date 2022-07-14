package com.example.goalapp.viewmodels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.example.goalapp.data.GoalRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class GoalDetailsViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val goalRepository: GoalRepository
) : ViewModel() {

    val goalId: Long = savedStateHandle.get<Long>(GOAL_DETAILS_ID_SAVED_STATE_KEY)!!

    val goal = goalRepository.getGoalWithProgress(goalId).asLiveData()

    companion object {
        const val GOAL_DETAILS_ID_SAVED_STATE_KEY = "goalId"
    }
}