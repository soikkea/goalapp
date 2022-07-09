package com.example.goalapp.ui.goallist

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.goalapp.ui.theme.GoalAppTheme

@Composable
fun GoalListItem(
    goalId: Int,
    onClicked: (Int) -> Unit = {}
) {
    Card(modifier = Modifier.fillMaxWidth().clickable { onClicked(goalId) }) {
        Row(modifier = Modifier.padding(4.dp)) {
            Text(text = "$goalId")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewGoalListItem() {
    GoalAppTheme {
        GoalListItem(goalId = 1)
    }
}
