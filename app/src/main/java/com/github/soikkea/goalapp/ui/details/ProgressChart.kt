package com.github.soikkea.goalapp.ui.details

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.soikkea.goalapp.data.GoalWithProgress
import com.github.soikkea.goalapp.ui.theme.GoalAppTheme
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.Scroll
import com.patrykandpatrick.vico.compose.cartesian.Zoom
import com.patrykandpatrick.vico.compose.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.compose.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberAxisLabelComponent
import com.patrykandpatrick.vico.compose.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.compose.cartesian.data.CartesianValueFormatter
import com.patrykandpatrick.vico.compose.cartesian.data.lineSeries
import com.patrykandpatrick.vico.compose.cartesian.layer.LineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLine
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.cartesian.rememberVicoScrollState
import com.patrykandpatrick.vico.compose.cartesian.rememberVicoZoomState
import com.patrykandpatrick.vico.compose.common.Fill
import com.patrykandpatrick.vico.compose.common.vicoTheme
import com.patrykandpatrick.vico.compose.m3.common.rememberM3VicoTheme
import kotlinx.coroutines.runBlocking
import java.time.LocalDate
import java.time.Month
import java.time.format.DateTimeFormatter

data class ChartData(
    val startDate: LocalDate,
    val endDate: LocalDate,
    val startValue: Int,
    val goalValue: Int,
    val progressData: Map<LocalDate, Int>
) {
    companion object {
        fun fromGoalWithProgress(goal: GoalWithProgress): ChartData {
            var total = 0
            val cumSumProgress = mutableMapOf<LocalDate, Int>()
            cumSumProgress[goal.goal.startDate] = 0
            val sortedProgress = goal.progress.sortedBy { it.date }
            for (progress in sortedProgress) {
                total += progress.value
                cumSumProgress[progress.date] = total
            }
            return ChartData(
                goal.goal.startDate, goal.goal.endDate, 0, goal.goal.target, cumSumProgress
            )
        }
    }

    suspend fun toLineSeriesTransaction(modelProducer: CartesianChartModelProducer) {
        val guideX = listOf(startDate, endDate).map { it.toEpochDay() }
        val guideY = listOf(startValue, goalValue)
        val progressX = sequence {
            yield(startDate)
            yieldAll(progressData.keys)
        }.map { it.toEpochDay() }.toList()
        val progressY = sequence {
            yield(startValue)
            yieldAll(progressData.values)
        }.toList()

        modelProducer.runTransaction {
            lineSeries {
                series(guideX, guideY)
                series(progressX, progressY)
            }
        }
    }
}

private val BottomAxisValueFormatter = CartesianValueFormatter { _, value, _ ->
    LocalDate.ofEpochDay(value.toLong()).format(DateTimeFormatter.ofPattern("d.M"))
}

@Composable
private fun ProgressChart(
    modelProducer: CartesianChartModelProducer, modifier: Modifier = Modifier
) {
    val theme = rememberM3VicoTheme()
    CartesianChartHost(
        rememberCartesianChart(
            rememberLineCartesianLayer(
                LineCartesianLayer.LineProvider.series(
                    listOf(
                        LineCartesianLayer.rememberLine(stroke = LineCartesianLayer.LineStroke.Dashed()),
                        LineCartesianLayer.rememberLine(
                            fill = LineCartesianLayer.LineFill.single(
                                Fill(
                                    vicoTheme.lineCartesianLayerColors[1]
                                )
                            ), areaFill = LineCartesianLayer.AreaFill.single(
                                Fill(
                                    Brush.verticalGradient(
                                        listOf(
                                            vicoTheme.lineCartesianLayerColors[1].copy(
                                                alpha = 0.4f
                                            ), Color.Transparent
                                        )
                                    )
                                )
                            )
                        )
                    )
                )
            ), startAxis = VerticalAxis.rememberStart(
                label = rememberAxisLabelComponent(style = TextStyle(color = theme.textColor))
            ), bottomAxis = HorizontalAxis.rememberBottom(
                label = rememberAxisLabelComponent(style = TextStyle(color = theme.textColor)),
                valueFormatter = BottomAxisValueFormatter
            )
        ),
        modelProducer,
        modifier,
        zoomState = rememberVicoZoomState(false, Zoom.Content),
        scrollState = rememberVicoScrollState(initialScroll = Scroll.Absolute.End),
    )
}

@Composable
fun ProgressChart(
    data: ChartData, modifier: Modifier = Modifier
) {
    val modelProducer = remember { CartesianChartModelProducer() }
    LaunchedEffect(Unit) {
        data.toLineSeriesTransaction(modelProducer)
    }
    ProgressChart(modelProducer, modifier)
}

@Composable
@Preview
private fun ProgressChartPreview() {
    val previewData = ChartData(
        LocalDate.of(2026, Month.JANUARY, 1), LocalDate.of(2026, Month.APRIL, 1), 0, 50, mapOf(
            LocalDate.of(2026, Month.JANUARY, 1) to 0,
            LocalDate.of(2026, Month.JANUARY, 5) to 5,
            LocalDate.of(2026, Month.FEBRUARY, 20) to 15,
            LocalDate.of(2026, Month.MARCH, 12) to 33,
        )
    )
    val modelProducer = remember { CartesianChartModelProducer() }
    runBlocking {
        previewData.toLineSeriesTransaction(modelProducer)
    }
    GoalAppTheme(darkTheme = true) {
        Box(modifier = Modifier.padding(16.dp)) {
            ProgressChart(modelProducer)
        }
    }
}

@Composable
@Preview
private fun ProgressChartPreview2() {
    val previewData = ChartData(
        LocalDate.of(2026, Month.JANUARY, 1), LocalDate.of(2026, Month.APRIL, 1), 0, 50, mapOf(
            LocalDate.of(2026, Month.JANUARY, 1) to 10,
            LocalDate.of(2026, Month.JANUARY, 2) to 12,
        )
    )
    val modelProducer = remember { CartesianChartModelProducer() }
    runBlocking {
        previewData.toLineSeriesTransaction(modelProducer)
    }
    GoalAppTheme(darkTheme = true) {
        Box(modifier = Modifier.padding(16.dp)) {
            ProgressChart(modelProducer)
        }
    }
}