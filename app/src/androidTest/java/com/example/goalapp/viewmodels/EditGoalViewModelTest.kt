package com.example.goalapp.viewmodels

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.asLiveData
import androidx.room.Room
import androidx.test.platform.app.InstrumentationRegistry
import com.example.goalapp.MainCoroutineRule
import com.example.goalapp.data.Goal
import com.example.goalapp.data.GoalDao
import com.example.goalapp.data.GoalDatabase
import com.example.goalapp.data.GoalRepository
import com.example.goalapp.runBlockingTest
import com.example.goalapp.utilities.getValue
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import org.junit.*
import org.junit.Assert.*
import org.junit.rules.RuleChain
import java.time.LocalDate
import javax.inject.Inject

@HiltAndroidTest
class EditGoalViewModelTest {

    private lateinit var goalDatabase: GoalDatabase
    private val hiltRule = HiltAndroidRule(this)
    private val instantTaskExecutorRule = InstantTaskExecutorRule()
    private val coroutineRule = MainCoroutineRule()

    @get:Rule
    val rule = RuleChain
        .outerRule(hiltRule)
        .around(instantTaskExecutorRule)
        .around(coroutineRule)

    @Inject
    lateinit var goalRepository: GoalRepository

    @Before
    fun setUp() {
        hiltRule.inject()

        val context = InstrumentationRegistry.getInstrumentation().targetContext
        goalDatabase = Room.inMemoryDatabaseBuilder(context, GoalDatabase::class.java).build()
    }

    @Test
    fun testTrimmedTitle() = coroutineRule.runBlockingTest {
        val savedStateHandle: SavedStateHandle = SavedStateHandle()

        val viewModel: EditGoalViewModel = EditGoalViewModel(savedStateHandle, goalRepository)

        assertFalse(viewModel.getValidationStatus())

        viewModel.onTitleChanged("ABC")

        viewModel.onTargetChanged(100)

        viewModel.onStartDateChanged(LocalDate.of(2022, 1, 1))
        viewModel.onEndDateChanged(LocalDate.of(2022, 1, 2))

        assertTrue(viewModel.getValidationStatus())

        viewModel.onTitleChanged("   ")

        assertEquals("", viewModel.getTrimmedTitle())

        assertFalse(viewModel.getValidationStatus())

        viewModel.onTitleChanged("  ABC   ")

        assertTrue(viewModel.saveGoal())

        val goals = getValue(goalRepository.allGoals.asLiveData())

        assertEquals("ABC", goals[0].title)
    }

    @After
    fun tearDown() {
        goalDatabase.close()
    }
}