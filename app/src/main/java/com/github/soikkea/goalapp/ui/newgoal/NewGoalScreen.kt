package com.github.soikkea.goalapp.ui.newgoal

import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.soikkea.goalapp.R
import com.github.soikkea.goalapp.ui.components.DatePickerModal
import com.github.soikkea.goalapp.ui.components.TopAppBarWithBackButton
import com.github.soikkea.goalapp.ui.theme.GoalAppTheme
import com.github.soikkea.goalapp.utilities.localDateTimeToLong
import com.github.soikkea.goalapp.utilities.longToLocalDateTime
import com.github.soikkea.goalapp.viewmodels.EditGoalViewModel
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.LocalDate

@Composable
fun NewGoalScreen(
    onBack: () -> Unit,
    viewModel: EditGoalViewModel,
    snackbarHostState: SnackbarHostState
) {
    val context = LocalContext.current

    val startDate = viewModel.startDate
    val endDate = viewModel.endDate

    var showStartDatePickerModal by remember { mutableStateOf(false) }
    var showEndDatePickerModal by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()

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
            showStartDatePickerModal = true
        },
        endDate,
        {
            showEndDatePickerModal = true
        },
        onBack,
        {
            onSaveClick(
                viewModel::saveGoal,
                {
                    scope.launch {
                        snackbarHostState.showSnackbar(context.getString(R.string.goal_save_failed))
                    }
                },
                onBack
            )
        }
    )
    if (showStartDatePickerModal) {
        val longDate = localDateTimeToLong(startDate.atStartOfDay(), true)
        DatePickerModal(
            longDate,
            { newDate ->
                newDate?.let {
                    viewModel.onStartDateChanged(
                        longToLocalDateTime(
                            newDate,
                            true
                        ).toLocalDate()
                    )
                }
            },
            onDismiss = { showStartDatePickerModal = false }
        )
    }
    if (showEndDatePickerModal) {
        val longDate = localDateTimeToLong(endDate.atStartOfDay(), true)
        val longStartDate = localDateTimeToLong(startDate.atStartOfDay(), true)
        DatePickerModal(
            longDate,
            {
                it?.let {
                    viewModel.onEndDateChanged(
                        longToLocalDateTime(
                            it,
                            true
                        ).toLocalDate()
                    )
                }
            },
            onDismiss = { showEndDatePickerModal = false },
            startConstraintUtcMillis = longStartDate
        )
    }
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
    val resources = LocalResources.current
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
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester),
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
    saveGoal: () -> Boolean,
    onError: () -> Unit,
    onFinish: () -> Unit
) {
    // TODO: Should disable save button when input not valid
    val saveSuccess = saveGoal()
    if (!saveSuccess) {
        onError()
        return
    }
    onFinish()
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
