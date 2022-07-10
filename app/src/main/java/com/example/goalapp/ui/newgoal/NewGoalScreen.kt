package com.example.goalapp.ui.newgoal

import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.goalapp.R
import com.example.goalapp.ui.components.TopAppBarWithBackButton
import com.example.goalapp.ui.theme.GoalAppTheme
import com.google.android.material.datepicker.MaterialDatePicker

@Composable
fun NewGoalScreen(
    onBack: () -> Unit
) {
    val activity = LocalContext.current as AppCompatActivity

    EditGoalView(activity, onBack)
}

@Composable
private fun EditGoalView(
    activity: AppCompatActivity?,
    onBack: () -> Unit
) {
    val resources = LocalContext.current.resources
    Scaffold(
        topBar = {
            TopAppBarWithBackButton(
                title = stringResource(id = R.string.add_goal),
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
                value = "",
                onValueChange = {},
                label = { Text(text = stringResource(id = R.string.goal_title)) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            TextField(
                value = "",
                onValueChange = {},
                label = { Text(text = stringResource(id = R.string.target)) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
            DateRow(
                modifier = Modifier.padding(vertical = 16.dp),
                label = R.string.start_date,
                onClick = { showDatePicker(activity) })
            DateRow(
                modifier = Modifier.padding(vertical = 16.dp),
                label = R.string.end_date,
                onClick = { showDatePicker(activity) })
            // TODO replace placeholder values below
            Text(text = resources.getQuantityString(R.plurals.goal_length_average, 0, 0, 0.0))
            Button(
                onClick = { onSaveClick(onBack) },
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
    onClick: () -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = stringResource(id = label))
        // TODO: placeholder
        Text("01-01-2022")
    }
}

private fun onSaveClick(onFinish: () -> Unit) {
    // TODO
    onFinish()
}

private fun showDatePicker(
    activity: AppCompatActivity?
) {
    val picker = MaterialDatePicker.Builder.datePicker().build()
    picker.show(activity!!.supportFragmentManager, picker.toString())
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    GoalAppTheme {
        EditGoalView(activity = null) {

        }
    }
}
