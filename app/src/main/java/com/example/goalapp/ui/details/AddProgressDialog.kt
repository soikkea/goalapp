package com.example.goalapp.ui.details

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.goalapp.R
import com.example.goalapp.ui.theme.GoalAppTheme
import java.time.LocalDate

@Composable
fun AddProgressDialog(
    goalTitle: String,
    date: LocalDate,
    oldProgressValue: Int?,
    onProgressValueSaved: (Int) -> Unit,
    totalProgress: Int,
    dailyTarget: Double,
    targetProgress: Int,
    onClose: () -> Unit = {}
) {
    var progressValue: Int? by rememberSaveable {
        mutableStateOf(oldProgressValue)
    }
    val totalProgressExcludingCurrentDate = totalProgress - (oldProgressValue ?: 0)
    AlertDialog(onDismissRequest = { onClose() },
        title = { Text(text = stringResource(id = R.string.update_progress)) },
        text = {
            AddProgressDialogContent(
                goalTitle,
                date,
                progressValue,
                { newValue ->
                    progressValue = if ((newValue
                            ?: 0) + (totalProgressExcludingCurrentDate) > targetProgress
                    ) {
                        (targetProgress - totalProgressExcludingCurrentDate)
                    } else if (newValue == null || newValue < 0) {
                        0
                    } else {
                        newValue
                    }
                },
                totalProgressExcludingCurrentDate,
                dailyTarget
            )
        },
        confirmButton = {
            TextButton(onClick = {
                onProgressValueSaved(progressValue ?: 0)
                onClose()
            }) {
                Text(text = "OK")
            }
        },
        dismissButton = {
            TextButton(onClick = { onClose() }) {
                Text(text = "Cancel")
            }
        })
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun AddProgressDialogContent(
    goalTitle: String,
    date: LocalDate,
    progressValue: Int?,
    onProgressValueChange: (Int?) -> Unit,
    priorProgress: Int,
    dailyTarget: Double,
) {
    val progressValueInt = (progressValue ?: 0)
    val totalProgress = progressValueInt + (priorProgress)
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(goalTitle)
        Text(date.toString())
        val buttonModifier = Modifier
            .width(50.dp)
            .padding(horizontal = 2.dp)
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                modifier = buttonModifier,
                contentPadding = PaddingValues(0.dp),
                onClick = { onProgressValueChange(progressValueInt - 10) }) {
                Text(
                    text = stringResource(id = R.string.minus_ten)
                )
            }
            Button(
                modifier = buttonModifier,
                contentPadding = PaddingValues(0.dp),
                onClick = { onProgressValueChange(progressValueInt - 1) }) {
                Text(text = stringResource(id = R.string.minus_one))
            }
            val keyBoardController = LocalSoftwareKeyboardController.current
            TextField(
                modifier = Modifier.width(64.dp),
                value = if (progressValue != null) "$progressValue" else "",
                onValueChange = { onProgressValueChange(it.toIntOrNull()) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                keyboardActions = KeyboardActions(
                    onDone = {
                        keyBoardController?.hide()
                    })
            )
            Button(
                modifier = buttonModifier,
                contentPadding = PaddingValues(0.dp),
                onClick = { onProgressValueChange(progressValueInt + 1) }) {
                Text(text = stringResource(id = R.string.plus_one))
            }
            Button(
                modifier = buttonModifier,
                contentPadding = PaddingValues(0.dp),
                onClick = { onProgressValueChange(progressValueInt + 10) }) {
                Text(text = stringResource(id = R.string.plus_ten))
            }

        }
        Text(stringResource(id = R.string.target_and_total, dailyTarget, totalProgress))
    }
}


@Preview(showBackground = true)
@Composable
private fun AddProgressDialogPreview() {
    GoalAppTheme {
        AddProgressDialog(
            "Example Goal",
            LocalDate.now(),
            null,
            {},
            0,
            15.5,
            100,
            {}
        )
    }
}