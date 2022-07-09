package com.example.goalapp.ui.details

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.example.goalapp.ui.components.TopAppBarWithBackButton

@Composable
fun GoalDetailsScreen(
    goalId: Int,
    onBack: () -> Unit
) {
    val openDialog = remember {
        mutableStateOf(false)
    }
    Scaffold(
        topBar = {
            TopAppBarWithBackButton(
                title = "Details",
                onBack = onBack
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { openDialog.value = true }) {
                Icon(Icons.Default.Add, contentDescription = "Add progress")
            }
        }
    ) { contentPadding ->
        Column(modifier = Modifier.padding(contentPadding)) {
            Text(text = "$goalId")
        }

    }
    if (openDialog.value) {
        AddProgressDialog(onClose = { openDialog.value = false })
    }
}