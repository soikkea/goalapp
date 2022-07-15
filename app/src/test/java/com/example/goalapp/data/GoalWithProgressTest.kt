package com.example.goalapp.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

class GoalWithProgressTest {

    private val startDate: LocalDate = LocalDate.of(2022, 1, 1)

    private fun createExampleGoal(totalDays: Long, target: Int): Goal {
        return Goal(
            1,
            "test",
            startDate,
            startDate.plusDays(totalDays - 1),
            target, 0L, false
        )
    }

    private fun createExampleGoalWithProgress(
        totalDays: Long,
        target: Int,
        progress: List<GoalProgress>
    ): GoalWithProgress {
        return GoalWithProgress(createExampleGoal(totalDays, target), progress)
    }

    private fun createExampleProgres(dateOffset: Long, value: Int): GoalProgress {
        return GoalProgress(1, startDate.plusDays(dateOffset), value)
    }

    @Test
    fun totalProgress() {
        val goal = createExampleGoalWithProgress(
            31,
            50,
            listOf(
                createExampleProgres(0, 1),
                createExampleProgres(1, 2),
                createExampleProgres(2, 3)
            )
        )

        assertEquals(6, goal.totalProgress())
    }

    @Test
    fun totalProgressBeforeDate() {
        val goal = createExampleGoalWithProgress(
            31,
            50,
            listOf(
                createExampleProgres(0, 1),
                createExampleProgres(1, 2),
                createExampleProgres(2, 3)
            )
        )

        assertEquals(0, goal.totalProgressBeforeDate(startDate))
        assertEquals(1, goal.totalProgressBeforeDate(startDate.plusDays(1)))
        assertEquals(3, goal.totalProgressBeforeDate(startDate.plusDays(2)))
        assertEquals(6, goal.totalProgressBeforeDate(startDate.plusDays(3)))
    }

    @Test
    fun daysRemaining() {
        val goal = createExampleGoalWithProgress(31, 50, emptyList())

        assertEquals(31L, goal.totalDays())

        assertEquals(1L, goal.daysBeforeStart(startDate.minusDays(1)))
        assertEquals(0L, goal.daysBeforeStart(startDate))
        assertEquals(0L, goal.daysBeforeStart(startDate.plusDays(1)))

        assertEquals(32L, goal.daysRemaining(startDate.minusDays(1)))
        assertEquals(31L, goal.daysRemaining(startDate))
        assertEquals(30L, goal.daysRemaining(startDate.plusDays(1)))
        assertEquals(1L, goal.daysRemaining(startDate.plusDays(30)))
        assertEquals(-1L, goal.daysRemaining(startDate.plusDays(31)))
        assertTrue(startDate.plusDays(31).isAfter(goal.goal.endDate))
    }

    @Test
    fun requiredDailyProgress() {
        val goal = createExampleGoalWithProgress(
            10,
            10,
            listOf(
                createExampleProgres(0, 2),
                createExampleProgres(1, 2),
                createExampleProgres(2, 2),
                createExampleProgres(3, 2),
                createExampleProgres(4, 2),
            )
        )

        val delta = 1e-3
        assertEquals(1.0, goal.requiredDailyProgress(startDate.minusDays(1)), delta)
        assertEquals(1.0, goal.requiredDailyProgress(startDate), delta)
        assertEquals((8.0 / 9.0), goal.requiredDailyProgress(startDate.plusDays(1)), delta)
        assertEquals((6.0 / 8.0), goal.requiredDailyProgress(startDate.plusDays(2)), delta)
        assertEquals((4.0 / 7.0), goal.requiredDailyProgress(startDate.plusDays(3)), delta)
        assertEquals((2.0 / 6.0), goal.requiredDailyProgress(startDate.plusDays(4)), delta)
        assertEquals(0.0, goal.requiredDailyProgress(startDate.plusDays(5)), delta)
        assertEquals(0.0, goal.requiredDailyProgress(startDate.plusDays(6)), delta)
    }
}