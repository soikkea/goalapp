package com.example.goalapp

import com.example.goalapp.utilities.localDateTimeToLong
import com.example.goalapp.utilities.longToLocalDateTime
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate

class DateTimeUtilTest {
    @Test
    fun test_localDateTimeToLong() {
        val localDate = LocalDate.of(2022, 7, 10)
        val dateTime = localDate.atStartOfDay()
        val dtLong = localDateTimeToLong(dateTime)
        val dateTime2 = longToLocalDateTime(dtLong)
        assertEquals(dateTime, dateTime2)
    }
}