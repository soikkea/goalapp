package com.example.goalapp.data

import androidx.room.TypeConverter
import com.example.goalapp.utilities.localDateTimeToLong
import com.example.goalapp.utilities.longToLocalDateTime
import java.time.LocalDate

class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?): LocalDate? {
        return value?.let { longToLocalDateTime(it, true).toLocalDate() }
    }

    @TypeConverter
    fun toTimestamp(date: LocalDate?): Long? {
        return date?.let { localDateTimeToLong(date.atStartOfDay(), true) }
    }
}