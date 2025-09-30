@file:OptIn(ExperimentalMaterial3Api::class)

package com.github.soikkea.goalapp.ui.components

import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.github.soikkea.goalapp.R
import java.time.Instant
import java.time.ZoneOffset

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerModal(
    initialSelectedDateMillis: Long?,
    onDateSelected: (Long?) -> Unit,
    onDismiss: () -> Unit,
    startConstraintUtcMillis: Long? = null
) {
    val selectableDates = if (startConstraintUtcMillis != null) {
        SelectFutureDates(startConstraintUtcMillis)
    } else {
        DatePickerDefaults.AllDates
    }
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = initialSelectedDateMillis,
        selectableDates = selectableDates
    )

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                onDateSelected(datePickerState.selectedDateMillis)
                onDismiss()
            }) {
                Text(stringResource(R.string.ok))
            }
        }, dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        }
    ) {
        DatePicker(state = datePickerState)
    }
}

class SelectFutureDates(val startDateUtcMillis: Long) : SelectableDates {
    override fun isSelectableYear(year: Int): Boolean {
        return Instant.ofEpochMilli(startDateUtcMillis).atOffset(ZoneOffset.UTC).year <= year
    }

    override fun isSelectableDate(utcTimeMillis: Long): Boolean {
        return utcTimeMillis > startDateUtcMillis
    }
}