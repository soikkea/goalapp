package com.example.goalapp.viewmodels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class NavigationViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle
): ViewModel() {

    fun setGoalDetailsGoalId(goalId: Long) {
        savedStateHandle[EditGoalViewModel.GOAL_ID_SAVED_STATE_KEY] = goalId
    }
}