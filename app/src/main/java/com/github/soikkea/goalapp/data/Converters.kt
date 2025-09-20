package com.github.soikkea.goalapp.data

import androidx.room.TypeConverter
import com.github.soikkea.goalapp.utilities.localDateTimeToLong
import com.github.soikkea.goalapp.utilities.longToLocalDateTime
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