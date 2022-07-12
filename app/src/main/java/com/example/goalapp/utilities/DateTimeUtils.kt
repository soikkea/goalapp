package com.example.goalapp.utilities

import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.*

fun localDateTimeToLong(localDateTime: LocalDateTime, utc: Boolean = false): Long {
    if (utc) {
        return localDateTime.toInstant(ZoneOffset.UTC).toEpochMilli()
    }
    val zone = TimeZone.getDefault().toZoneId()
    return localDateTime.atZone(zone).toInstant().toEpochMilli()
}

fun longToLocalDateTime(value: Long, utc: Boolean = false): LocalDateTime {
    if (utc) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(value), TimeZone.getTimeZone(ZoneOffset.UTC.id).toZoneId())
    }
    val zone = TimeZone.getDefault().toZoneId()
    return LocalDateTime.ofInstant(Instant.ofEpochMilli(value), zone)
}

fun utcLongNow(): Long {
    return Instant.now().toEpochMilli()
}