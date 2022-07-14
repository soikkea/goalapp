package com.example.goalapp.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.example.goalapp.data.GoalRepository
import com.example.goalapp.data.GoalWithProgress
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class GoalListViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val goalRepository: GoalRepository
) : ViewModel() {

    val allGoalsWithProgress: LiveData<List<GoalWithProgress>> =
        goalRepository.allGoalsWithProgress.asLiveData()
}