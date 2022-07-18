package com.example.goalapp.data

import org.junit.Assert.*
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

    private fun createExampleProgress(dateOffset: Long, value: Int): GoalProgress {
        return GoalProgress(1, startDate.plusDays(dateOffset), value)
    }

    @Test
    fun totalProgress() {
        val goal = createExampleGoalWithProgress(
            31,
            50,
            listOf(
                createExampleProgress(0, 1),
                createExampleProgress(1, 2),
                createExampleProgress(2, 3)
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
                createExampleProgress(0, 1),
                createExampleProgress(1, 2),
                createExampleProgress(2, 3)
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
                createExampleProgress(0, 2),
                createExampleProgress(1, 2),
                createExampleProgress(2, 2),
                createExampleProgress(3, 2),
                createExampleProgress(4, 2),
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

    @Test
    fun expectedProgressForDay() {
        val goal = createExampleGoalWithProgress(
            10,
            10,
            emptyList()
        )

        assertEquals(1, goal.expectedProgressForDay(startDate))
        assertEquals(2, goal.expectedProgressForDay(startDate.plusDays(1)))
        assertEquals(3, goal.expectedProgressForDay(startDate.plusDays(2)))
        assertEquals(4, goal.expectedProgressForDay(startDate.plusDays(3)))
        assertEquals(5, goal.expectedProgressForDay(startDate.plusDays(4)))
        assertEquals(6, goal.expectedProgressForDay(startDate.plusDays(5)))
        assertEquals(7, goal.expectedProgressForDay(startDate.plusDays(6)))
        assertEquals(8, goal.expectedProgressForDay(startDate.plusDays(7)))
        assertEquals(9, goal.expectedProgressForDay(startDate.plusDays(8)))
        assertEquals(10, goal.expectedProgressForDay(startDate.plusDays(9)))
    }

    @Test
    fun earlyOrLate() {
        val progressList = mutableListOf<GoalProgress>()
        val goal = createExampleGoalWithProgress(8, 8, progressList)

        assertEquals(ProgressStatus.ON_TIME, goal.getProgressStatus(startDate))

        val midDate = startDate.plusDays(3)
        assertEquals(ProgressStatus.LATE, goal.getProgressStatus(midDate))

        progressList.add(GoalProgress(1, midDate, 4))
        assertEquals(ProgressStatus.ON_TIME, goal.getProgressStatus(midDate))

        progressList[0].value = 7
        assertEquals(ProgressStatus.EARLY, goal.getProgressStatus(midDate))

        progressList.add(GoalProgress(1, goal.goal.endDate, 1))
        assertEquals(goal.goal.target, goal.totalProgress())
        assertEquals(ProgressStatus.ON_TIME, goal.getProgressStatus(goal.goal.endDate))
    }

    @Test
    fun onTimeStatus() {
        val progressList = mutableListOf<GoalProgress>()
        val goal = createExampleGoalWithProgress(10, 1000, progressList)

        assertEquals(ProgressStatus.ON_TIME, goal.getProgressStatus(startDate))

        assertEquals(100.0, goal.requiredDailyProgress(startDate), 0.1)

        progressList.add(GoalProgress(1, startDate, 10))

        assertEquals(ProgressStatus.LATE, goal.getProgressStatus(startDate))

        progressList[0].value = 100
        assertEquals(ProgressStatus.ON_TIME, goal.getProgressStatus(startDate))

        var now = startDate.plusDays(1)

        assertEquals(ProgressStatus.ON_TIME, goal.getProgressStatus(now))

        now = now.plusDays(1)

        assertEquals(ProgressStatus.LATE, goal.getProgressStatus(now))

        progressList.add(GoalProgress(1, now, 500))

        assertEquals(ProgressStatus.EARLY, goal.getProgressStatus(now))
    }
}