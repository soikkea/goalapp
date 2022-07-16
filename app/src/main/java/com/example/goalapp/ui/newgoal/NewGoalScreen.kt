package com.example.goalapp.ui.newgoal

import android.app.Activity
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.goalapp.R
import com.example.goalapp.ui.components.TopAppBarWithBackButton
import com.example.goalapp.ui.theme.GoalAppTheme
import com.example.goalapp.utilities.localDateTimeToLong
import com.example.goalapp.utilities.longToLocalDateTime
import com.example.goalapp.viewmodels.EditGoalViewModel
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import java.time.Duration
import java.time.LocalDate

@Composable
fun NewGoalScreen(
    onBack: () -> Unit,
    viewModel: EditGoalViewModel
) {
    val activity = LocalContext.current as AppCompatActivity

    val startDate = viewModel.startDate
    val endDate = viewModel.endDate

    EditGoalView(
        R.string.add_goal,
        viewModel.goalTitle,
        viewModel::onTitleChanged,
        viewModel.goalTarget,
        { targetString: String ->
            viewModel.onTargetChanged(targetString.toIntOrNull())
        },
        startDate,
        {
            val longDate = localDateTimeToLong(startDate.atStartOfDay(), true)
            showDatePicker(
                activity,
                longDate,
                { newDate ->
                    viewModel.onStartDateChanged(
                        longToLocalDateTime(
                            newDate,
                            true
                        ).toLocalDate()
                    )
                })
        },
        endDate,
        {
            val longDate = localDateTimeToLong(endDate.atStartOfDay(), true)
            val longStartDate = localDateTimeToLong(startDate.atStartOfDay(), true)
            showDatePicker(
                activity,
                longDate,
                { newDate ->
                    viewModel.onEndDateChanged(
                        longToLocalDateTime(
                            newDate,
                            true
                        ).toLocalDate()
                    )
                },
                longStartDate
            )
        },
        onBack,
        {
            onSaveClick(
                activity,
                viewModel::saveGoal,
                onBack
            )
        }
    )
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun EditGoalView(
    @StringRes topBarTitle: Int,
    goalTitle: String,
    onGoalTitleChange: (String) -> Unit,
    goalProgress: Int?,
    onGoalProgressChange: (String) -> Unit,
    startDate: LocalDate,
    onStartDateClick: () -> Unit,
    endDate: LocalDate,
    onEndDateClick: () -> Unit,
    onBack: () -> Unit,
    onSaveClick: () -> Unit
) {
    val resources = LocalContext.current.resources
    val totalDays = Duration.between(startDate.atStartOfDay(), endDate.atStartOfDay()).toDays() + 1
    val progressPerDay = (goalProgress ?: 0).toDouble() / totalDays.toDouble()
    val keyBoardController = LocalSoftwareKeyboardController.current
    val focusRequester = remember {
        FocusRequester()
    }
    Scaffold(
        topBar = {
            TopAppBarWithBackButton(
                title = stringResource(id = topBarTitle),
                onBack = onBack
            )
        },
    ) { contentPadding ->
        Column(
            modifier = Modifier
                .padding(contentPadding)
                .fillMaxWidth()
                .padding(top = 8.dp)
                .padding(horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            TextField(
                value = goalTitle,
                onValueChange = onGoalTitleChange,
                label = { Text(text = stringResource(id = R.string.goal_title)) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                keyboardActions = KeyboardActions(
                    onDone = {
                        keyBoardController?.hide()
                        focusRequester.requestFocus()
                    }
                )
            )
            TextField(
                value = if (goalProgress != null) "$goalProgress" else "",
                onValueChange = onGoalProgressChange,
                label = { Text(text = stringResource(id = R.string.target)) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth().focusRequester(focusRequester),
                keyboardActions = KeyboardActions(
                    onDone = {
                        keyBoardController?.hide()
                    }
                )
            )
            DateRow(
                modifier = Modifier.padding(vertical = 16.dp),
                label = R.string.start_date,
                date = startDate,
                onClick = onStartDateClick
            )
            DateRow(
                modifier = Modifier.padding(vertical = 16.dp),
                label = R.string.end_date,
                date = endDate,
                onClick = onEndDateClick
            )
            Text(
                text = resources.getQuantityString(
                    R.plurals.goal_length_average,
                    totalDays.toInt(),
                    totalDays,
                    progressPerDay
                )
            )
            Button(
                onClick = onSaveClick,
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Text(text = stringResource(R.string.save))
            }
        }
    }
}

@Composable
fun DateRow(
    modifier: Modifier = Modifier,
    @StringRes label: Int,
    date: LocalDate,
    onClick: () -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = stringResource(id = label))
        Text(date.toString())
    }
}

private fun onSaveClick(
    activity: Activity,
    saveGoal: () -> Boolean,
    onFinish: () -> Unit
) {
    // TODO: Should disable save button when input not valid
    val saveSuccess = saveGoal()
    if (!saveSuccess) {
        Toast.makeText(activity.application, R.string.goal_save_failed, Toast.LENGTH_LONG).show()
        return
    }
    onFinish()
}

private fun showDatePicker(
    activity: AppCompatActivity?,
    date: Long,
    onDateSelected: (Long) -> Unit,
    startConstraint: Long? = null
) {
    val constraintsBuilder = CalendarConstraints.Builder()
    if (startConstraint != null) {
        constraintsBuilder.setStart(startConstraint)
        constraintsBuilder.setValidator(DateValidatorPointForward.from(startConstraint))
    }
    val picker = MaterialDatePicker.Builder.datePicker()
        .setSelection(date)
        .setCalendarConstraints(constraintsBuilder.build())
        .build()
    picker.show(activity!!.supportFragmentManager, picker.toString())
    picker.addOnPositiveButtonClickListener { newDate -> onDateSelected(newDate) }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    val startDate = LocalDate.now()
    val endDate = startDate.plusDays(1)
    GoalAppTheme {
        EditGoalView(
            topBarTitle = R.string.add_goal,
            onBack = {},
            goalTitle = "",
            goalProgress = null,
            onGoalTitleChange = {},
            onEndDateClick = {},
            onGoalProgressChange = {},
            endDate = endDate,
            onStartDateClick = {},
            startDate = startDate,
            onSaveClick = {}
        )
    }
}
