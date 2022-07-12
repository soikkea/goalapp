package com.example.goalapp

import com.example.goalapp.utilities.localDateTimeToLong
import com.example.goalapp.utilities.longToLocalDateTime
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneOffset

class DateTimeUtilTest {
    @Test
    fun test_localDateTimeToLong() {
        val localDate = LocalDate.of(2022, 7, 10)
        val dateTime = localDate.atStartOfDay()
        val dtLong = localDateTimeToLong(dateTime)
        val dateTime2 = longToLocalDateTime(dtLong)
        assertEquals(dateTime, dateTime2)
    }

    @Test
    fun test_localDateTimeToLongUTC() {
        val localDate = LocalDate.of(2022, 7, 10)
        val dateTime = localDate.atStartOfDay()
        val dtLong = localDateTimeToLong(dateTime, true)
        val dateTime2 = longToLocalDateTime(dtLong, true)
        assertEquals(dateTime, dateTime2)
    }

    @Test
    fun test_localDateTimeTimeZoneConversion() {
        val localDateTime = LocalDateTime.of(2022, 7, 11, 12, 15)
        val hoursOffset = 3
        val offset = ZoneOffset.ofHours(hoursOffset)
        val utcLong = localDateTime.atOffset(offset).toInstant().toEpochMilli()
        val utcLocalDateTime = localDateTime.minusHours(hoursOffset.toLong())
        assertEquals(utcLocalDateTime, longToLocalDateTime(utcLong, true))
        assertEquals(utcLong, localDateTimeToLong(utcLocalDateTime, true))
        val localDateTime2 = LocalDateTime.ofInstant(Instant.ofEpochMilli(utcLong), offset)
        assertEquals(localDateTime, localDateTime2)
    }
}