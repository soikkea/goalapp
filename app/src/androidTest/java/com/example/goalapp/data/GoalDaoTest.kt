package com.example.goalapp.data

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.equalTo
import org.junit.After
import org.junit.Assert
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import java.time.LocalDate

class GoalDaoTest {
    private lateinit var database: GoalDatabase
    private lateinit var goalDao: GoalDao
    private lateinit var progressDao: GoalProgressDao

    @Before
    fun createDb() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, GoalDatabase::class.java).build()
        goalDao = database.goalDao()
        progressDao = database.progressDao()
    }

    @After
    fun closeDb() {
        database.close()
    }

    @Test
    fun testGetGoalZero() = runBlocking {
        val goalId = 0L
        Assert.assertThat(goalDao.getGoal(goalId).firstOrNull(), equalTo(null))
    }

    @Test
    fun testInsertAndGetGoal() = runBlocking {
        val goal = createExampleGoal()
        goalDao.insertGoal(goal)
        val allGoals = goalDao.getOrderedGoals().first()
        assertEquals(1, allGoals.size)
        assertGoalsEqual(goal, allGoals[0])
        assertNotEquals(0L, allGoals[0].id)
    }

    private fun createExampleGoal(
        title: String? = null,
        startDate: LocalDate? = null,
        endDate: LocalDate? = null,
        target: Int? = null
    ): Goal {
        val title = title ?: "Test"
        val startDate = startDate ?: LocalDate.of(2022, 1, 1)
        val endDate = endDate ?: LocalDate.of(2022, 12, 31)
        val target = target ?: 10
        return Goal.create(title, startDate, endDate, target)
    }

    private fun assertGoalsEqual(expected: Goal, actual: Goal) {
        assertEquals(expected.title, actual.title)
        assertEquals(expected.startDate, actual.startDate)
        assertEquals(expected.endDate, actual.endDate)
        assertEquals(expected.target, actual.target)
        assertEquals(expected.completed, actual.completed)
    }

    @Test
    fun testGetAllGoalsOrdered() = runBlocking {
        val goal = createExampleGoal("AAA")
        goalDao.insertGoal(goal)
        val goal2 = createExampleGoal("bbb")
        goalDao.insertGoal(goal2)
        val goal3 = createExampleGoal(title = "AAA", startDate = goal.startDate.plusDays(10))
        goalDao.insertGoal(goal3)
        val allGoals = goalDao.getOrderedGoals().first()
        assertEquals(3, allGoals.size)
        assertGoalsEqual(allGoals[0], goal)
        assertGoalsEqual(allGoals[1], goal2)
        assertGoalsEqual(allGoals[2], goal3)
    }

    @Test
    fun testDeleteGoals() = runBlocking {
        val goal = createExampleGoal("Goal1")
        goalDao.insertGoal(goal)
        val goal2 = goal.copy(title = "Goal2")
        goalDao.insertGoal(goal2)
        val allGoals = goalDao.getOrderedGoals().first()
        assertEquals(2, allGoals.size)
        goalDao.deleteGoals(allGoals[0], allGoals[1])
        val allGoalsAfter = goalDao.getOrderedGoals().first()
        assertTrue(allGoalsAfter.isEmpty())
    }

    @Test
    fun testGetNonExistingGoal() = runBlocking {
        val goal = goalDao.getGoal(5).first()
        assertNull(goal)
    }

    @Test
    fun testGoalWithProgress() = runBlocking {
        val goal = createExampleGoal("AAA", target = 100)
        goalDao.insertGoal(goal)
        val allGoals = goalDao.getGoalsWithProgress().first()
        var goalWithProgress = allGoals[0]
        assertTrue(goalWithProgress.progress.isEmpty())
        assertEquals(0, goalWithProgress.totalProgress())

        val startDate = goalWithProgress.goal.startDate
        var progress = GoalProgress(goalWithProgress.goal.id, startDate, 5)
        progressDao.insertProgress(progress)
        progress = GoalProgress(goalWithProgress.goal.id, startDate.plusDays(1), 15)
        progressDao.insertProgress(progress)

        goalWithProgress = goalDao.getGoalWithProgress(goalWithProgress.goal.id).first()
        assertTrue(goalWithProgress.progress.isNotEmpty())

        assertEquals(20, goalWithProgress.totalProgress())
    }

    @Test
    fun testDeleteGoalWithProgress() = runBlocking {
        val goal = createExampleGoal("AAA", target = 100)
        goalDao.insertGoal(goal)
        val allGoals = goalDao.getGoalsWithProgress().first()
        var goalWithProgress = allGoals[0]
        assertTrue(goalWithProgress.progress.isEmpty())
        assertEquals(0, goalWithProgress.totalProgress())

        val startDate = goalWithProgress.goal.startDate
        var progress = GoalProgress(goalWithProgress.goal.id, startDate, 5)
        progressDao.insertProgress(progress)
        progress = GoalProgress(goalWithProgress.goal.id, startDate.plusDays(1), 15)
        progressDao.insertProgress(progress)

        goalWithProgress = goalDao.getGoalWithProgress(goalWithProgress.goal.id).first()
        assertTrue(goalWithProgress.progress.isNotEmpty())

        assertEquals(20, goalWithProgress.totalProgress())

        goalDao.deleteGoals(goalWithProgress.goal)

        var allProgress = progressDao.getAllProgress().first()
        assertTrue(allProgress.isEmpty())
    }

    @Test
    fun testGoalProgressDao() = runBlocking {

        val goal = createExampleGoal(target = 100)
        val goalId = goalDao.insertGoal(goal)

        val startDate = goal.startDate
        val progress = GoalProgress(goalId, startDate, 10)
        progressDao.insertProgress(progress)

        val progress2 = progressDao.getProgressForGoalAndDate(goalId, startDate).first()
        assertEquals(progress.date, progress2.date)
        assertEquals(progress.goalId, progress2.goalId)
        assertEquals(progress.value, progress2.value)
    }

    @Test
    fun testInsertProgress() = runBlocking {
        val goal = createExampleGoal(target = 100)
        val goalId = goalDao.insertGoal(goal)

        val startDate = goal.startDate
        val progress = GoalProgress(goalId, startDate, 5)
        progressDao.insertProgress(progress)

        var goalWithProgress = goalDao.getGoalWithProgress(goalId).first()
        assertEquals(5, goalWithProgress.totalProgress())
        assertEquals(1, goalWithProgress.progress.count())

        val newProgress = GoalProgress(goalId, startDate, 15)
        progressDao.insertProgress(newProgress)

        goalWithProgress = goalDao.getGoalWithProgress(goalId).first()
        assertEquals(15, goalWithProgress.totalProgress())
        assertEquals(1, goalWithProgress.progress.count())
    }
}