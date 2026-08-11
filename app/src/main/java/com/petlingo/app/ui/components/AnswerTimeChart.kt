package com.petlingo.app.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.unit.dp
import com.petlingo.app.model.AnswerRecord

@Composable
fun AnswerTimeChart(answers: List<AnswerRecord>, modifier: Modifier = Modifier) {
    if (answers.isEmpty()) return
    Canvas(modifier.fillMaxWidth().height(180.dp)) {
        val left = 24f
        val right = size.width - 12f
        val top = 16f
        val bottom = size.height - 28f
        val chartWidth = (right - left).coerceAtLeast(1f)
        val chartHeight = (bottom - top).coerceAtLeast(1f)
        val maxMillis = answers.maxOf { it.elapsedMillis }.coerceAtLeast(1L)

        drawLine(Color.Gray, Offset(left, top), Offset(left, bottom), strokeWidth = 2f)
        drawLine(Color.Gray, Offset(left, bottom), Offset(right, bottom), strokeWidth = 2f)

        val path = Path()
        answers.forEachIndexed { index, answer ->
            val x = if (answers.size == 1) left + chartWidth / 2f else left + chartWidth * index / (answers.size - 1)
            val y = bottom - chartHeight * answer.elapsedMillis.toFloat() / maxMillis
            if (index == 0) path.moveTo(x, y) else path.lineTo(x, y)
        }
        drawPath(path, Color(0xFF5B6BB2), style = androidx.compose.ui.graphics.drawscope.Stroke(width = 4f))

        answers.forEachIndexed { index, answer ->
            val x = if (answers.size == 1) left + chartWidth / 2f else left + chartWidth * index / (answers.size - 1)
            val y = bottom - chartHeight * answer.elapsedMillis.toFloat() / maxMillis
            drawCircle(if (answer.isCorrect) Color(0xFF2E7D32) else Color(0xFFC62828), radius = 7f, center = Offset(x, y))
        }
    }
}
