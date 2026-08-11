package com.petlingo.app.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.unit.dp
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun RadarChart(
    values: List<Float>,
    labels: List<String> = listOf("單字", "文法", "閱讀", "聽力", "速度"),
    modifier: Modifier = Modifier
) {
    val normalizedValues = if (values.size >= 5) values.take(5) else values + List(5 - values.size) { 0f }
    val normalizedLabels = if (labels.size >= 5) labels.take(5) else labels + List(5 - labels.size) { "" }

    Canvas(modifier.fillMaxWidth().height(310.dp)) {
        val n = 5
        val center = center
        val radius = size.minDimension * 0.34f

        fun point(i: Int, scale: Float): Offset {
            val angle = -PI / 2 + 2 * PI * i / n
            return Offset(
                center.x + (cos(angle) * radius * scale).toFloat(),
                center.y + (sin(angle) * radius * scale).toFloat()
            )
        }

        for (level in 1..5) {
            val path = Path()
            repeat(n) { i ->
                val pt = point(i, level / 5f)
                if (i == 0) path.moveTo(pt.x, pt.y) else path.lineTo(pt.x, pt.y)
            }
            path.close()
            drawPath(path, Color(0xFFB8B8B8), style = Stroke(1f))
        }

        repeat(n) { i ->
            drawLine(Color(0xFFB8B8B8), center, point(i, 1f), 1f)
        }

        val data = Path()
        normalizedValues.forEachIndexed { i, value ->
            val pt = point(i, (value / 5f).coerceIn(0f, 1f))
            if (i == 0) data.moveTo(pt.x, pt.y) else data.lineTo(pt.x, pt.y)
        }
        data.close()
        drawPath(data, Color(0x667E9360))
        drawPath(data, Color(0xFF657A4B), style = Stroke(4f))

        val paint = android.graphics.Paint().apply {
            color = android.graphics.Color.rgb(55, 55, 55)
            textSize = 30f
            textAlign = android.graphics.Paint.Align.CENTER
            isAntiAlias = true
            typeface = android.graphics.Typeface.DEFAULT_BOLD
        }

        normalizedLabels.forEachIndexed { i, label ->
            val p = point(i, 1.20f)
            drawContext.canvas.nativeCanvas.drawText(label, p.x, p.y + 10f, paint)
        }
    }
}
