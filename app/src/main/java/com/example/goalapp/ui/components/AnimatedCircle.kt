package com.example.goalapp.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun AnimatedCircle(
    progress: Float,
    expected: Float,
    color: Color,
    modifier: Modifier = Modifier
) {
    val currentState = remember {
        MutableTransitionState(AnimatedCircleProgress.START)
            .apply { targetState = AnimatedCircleProgress.END }
    }
    val stroke = with(LocalDensity.current) {
        Stroke(5.dp.toPx())
    }
    val expectedStroke = with(LocalDensity.current) {
        Stroke(7.dp.toPx())
    }
    val expectedColor = color.copy(alpha = 0.24f)
    val transition = updateTransition(currentState, label = "transition")
    val angleOffset by transition.animateFloat(
        label = "angleOffset",
        transitionSpec = {
            tween(
                delayMillis = 500,
                durationMillis = 900,
                easing = LinearOutSlowInEasing
            )
        }
    ) { p ->
        if (p == AnimatedCircleProgress.START) {
            0f
        } else {
            1f
        }
    }

    Canvas(modifier = modifier) {
        val startAngle = -90f
        fun drawProgressArc(
            color: Color,
            arcProgress: Float,
            stroke: Stroke
        ) {
            val innerRadius = (size.minDimension - stroke.width) / 2
            val halfSize = size / 2.0f
            val topLeft = Offset(
                halfSize.width - innerRadius,
                halfSize.height - innerRadius
            )
            val size = Size(innerRadius * 2, innerRadius * 2)
            drawArc(
                color = color,
                startAngle = startAngle,
                sweepAngle = -360 * arcProgress * angleOffset,
                topLeft = topLeft,
                size = size,
                useCenter = false,
                style = stroke
            )
        }
        drawProgressArc(
            color = expectedColor,
            arcProgress = expected,
            stroke = expectedStroke
        )
        drawProgressArc(
            color = color,
            arcProgress = progress,
            stroke = stroke
        )
    }
}

@Preview(widthDp = 200)
@Composable
fun DefaultPreview() {
    Box(content = {
        AnimatedCircle(
            progress = 0.25f, expected = 0.5f, color = Color.Green,
            modifier = Modifier
                .height(300.dp)
                .align(Alignment.Center)
                .fillMaxWidth()
        )
    })
}

private enum class AnimatedCircleProgress { START, END }