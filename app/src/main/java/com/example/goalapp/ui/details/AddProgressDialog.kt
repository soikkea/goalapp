package com.example.goalapp.ui.details

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.goalapp.ui.theme.GoalAppTheme

@Composable
fun AddProgressDialog(
    onClose: () -> Unit = {}
) {
    AlertDialog(onDismissRequest = { onClose() },
        title = { Text(text = "Add progress") },
        text = { AddProgressDialogContent() },
        confirmButton = {
            TextButton(onClick = { /*TODO*/ }) {
                Text(text = "OK")
            }
        },
        dismissButton = {
            TextButton(onClick = { onClose() }) {
                Text(text = "Cancel")
            }
        })
}

@Composable
fun AddProgressDialogContent() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally) {
        Button(onClick = { /*TODO*/ }) {
            Text(text = "Test")
        }
    }
}


@Preview(showBackground = true)
@Composable
private fun AddProgressDialogPreview() {
    GoalAppTheme {
        AddProgressDialog()
    }
}