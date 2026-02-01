package com.github.soikkea.goalapp.ui.calendar

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.github.soikkea.goalapp.R
import com.github.soikkea.goalapp.data.GoalWithProgress
import com.github.soikkea.goalapp.ui.components.TopAppBarWithBackButton
import com.github.soikkea.goalapp.ui.details.AddProgressDialog
import com.github.soikkea.goalapp.ui.theme.GoalAppTheme
import com.github.soikkea.goalapp.viewmodels.GoalDetailsViewModel
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.*

@Composable
fun CalendarScreen(
    onBack: () -> Unit,
    viewModel: GoalDetailsViewModel
) {
    var openDialog by remember {
        mutableStateOf(false)
    }
    var selectedMonth: LocalDate by rememberSaveable {
        mutableStateOf(viewModel.initialDate?.let { atFirstOfMonth(it) }
            ?: atFirstOfMonth(LocalDate.now()))
    }
    var selectedDate by rememberSaveable {
        mutableStateOf(selectedMonth)
    }
    val goal by viewModel.goal.observeAsState()
    val monthFormatter = DateTimeFormatter.ofPattern("MM.yyyy")
    Scaffold(
        topBar = {
            TopAppBarWithBackButton(title = stringResource(id = R.string.calendar), onBack = onBack)
        }
    ) { contentPadding ->
        Column(
            modifier = Modifier
                .padding(contentPadding)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { selectedMonth = selectedMonth.minusMonths(1) }) {
                    Icon(
                        painterResource(R.drawable.arrow_back_24px),
                        contentDescription = stringResource(R.string.previous_arrow)
                    )
                }
                Text(
                    text = selectedMonth.format(monthFormatter),
                    style = MaterialTheme.typography.headlineMedium
                )
                IconButton(onClick = { selectedMonth = selectedMonth.plusMonths(1) }) {
                    Icon(
                        painterResource(R.drawable.arrow_forward_24px),
                        contentDescription = stringResource(R.string.next_arrow)
                    )
                }
            }
            Calendar(selectedMonth, goal) { date ->
                selectedDate = date
                openDialog = true
            }
        }
    }

    if (goal != null && openDialog) {
        AddProgressDialog(
            goalTitle = goal!!.goal.title,
            date = selectedDate,
            dailyTarget = goal!!.requiredDailyProgress(selectedDate),
            oldProgressValue = goal!!.progressForDay(selectedDate),
            onProgressValueSaved = { value -> viewModel.addProgress(selectedDate, value) },
            targetProgress = goal!!.goal.target,
            totalProgress = goal!!.totalProgress(),
            onClose = { openDialog = false })
    }
}

private fun atFirstOfMonth(date: LocalDate): LocalDate {
    return LocalDate.of(date.year, date.month, 1)
}

@Composable
fun Calendar(
    date: LocalDate,
    goal: GoalWithProgress?,
    onDateClicked: (LocalDate) -> Unit
) {
    val daysInMonth = date.lengthOfMonth()
    val firstDayOfWeek = 0
    var currentDay = 1
    val startDate = goal?.goal?.startDate
    val endDate = goal?.goal?.endDate
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            for (i in 0..6) {
                val dayOfWeek = DayOfWeek.of(((firstDayOfWeek + i) % 7) + 1)
                Text(
                    modifier = Modifier.weight(1f),
                    text = dayOfWeek.getDisplayName(TextStyle.NARROW, Locale.getDefault()),
                    style = MaterialTheme.typography.headlineSmall,
                    textAlign = TextAlign.Center
                )
            }
        }
        while (currentDay <= daysInMonth) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                for (weekDayOffset in 0..6) {
                    if (currentDay > daysInMonth) {
                        DayCard(modifier = Modifier.weight(1f),
                            onDateClicked = {})
                        continue
                    }
                    val dayOfWeek = DayOfWeek.of(((firstDayOfWeek + weekDayOffset) % 7) + 1)
                    val currentDate = LocalDate.of(date.year, date.month, currentDay)
                    val cardDate = if (currentDate.dayOfWeek == dayOfWeek) currentDate else null
                    var progress: Int? = null
                    if (cardDate != null) {
                        progress = goal?.progressForDay(cardDate)
                        currentDay++
                    }
                    DayCard(
                        date = cardDate,
                        progress = progress,
                        backgroundColor = dayCardBackgroundColor(
                            cardDate = cardDate,
                            startDate = startDate,
                            endDate = endDate
                        ),
                        onDateClicked = onDateClicked,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun dayCardBackgroundColor(
    cardDate: LocalDate?,
    startDate: LocalDate?,
    endDate: LocalDate?
): Color {
    if (cardDate == null) {
        return MaterialTheme.colorScheme.background
    }
    val today = LocalDate.now()
    if (cardDate == today) {
        return MaterialTheme.colorScheme.secondary
    }
    if (startDate != null && cardDate == startDate) {
        return Color.Blue
    }
    if (endDate != null && cardDate == endDate) {
        return Color.Red
    }
    return MaterialTheme.colorScheme.background
}

@Composable
fun DayCard(
    modifier: Modifier = Modifier,
    date: LocalDate? = null,
    progress: Int? = null,
    backgroundColor: Color = MaterialTheme.colorScheme.background,
    onDateClicked: (LocalDate) -> Unit,
) {
    Card(
        border = BorderStroke(Dp.Hairline, MaterialTheme.colorScheme.onBackground),
        colors = CardDefaults.cardColors().copy(containerColor = backgroundColor),
        modifier = modifier.clickable {
            date?.let(onDateClicked)
        }
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(2.dp)
        ) {
            Text(text = date?.dayOfMonth?.toString() ?: "", style = MaterialTheme.typography.titleLarge)
            Text(text = progress?.toString() ?: "", style = MaterialTheme.typography.titleMedium)
        }
    }
}

@Preview
@Composable
fun DayCardPreview() {
    GoalAppTheme(true) {
        DayCard(
            date = LocalDate.now(),
            progress = 25,
            onDateClicked = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    GoalAppTheme(true) {
        Calendar(date = LocalDate.now(), null) {}
    }
}