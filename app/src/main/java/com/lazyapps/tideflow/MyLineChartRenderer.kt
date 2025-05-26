package com.lazyapps.tideflow

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.renderer.LineChartRenderer
import com.github.mikephil.charting.animation.ChartAnimator
import com.github.mikephil.charting.data.DataSet
import com.github.mikephil.charting.utils.ViewPortHandler

class CustomLineChartRenderer(
    chart: LineChart,
    animator: ChartAnimator,
    viewPortHandler: ViewPortHandler
) : LineChartRenderer(chart, animator, viewPortHandler) {

    private val bubblePaint = Paint().apply {
        color = Color.WHITE
        style = Paint.Style.FILL
        setShadowLayer(8f, 0f, 2f, Color.LTGRAY)
    }
    private val textPaint = Paint().apply {
        color = Color.BLACK
        textSize = 32f
        isAntiAlias = true
    }

    override fun drawExtras(c: Canvas) {
        super.drawExtras(c)
        // 必要に応じて複数のデータセットをサポート
        val data = mChart.lineData ?: return

        val now = java.util.Calendar.getInstance()
        val hour = now.get(java.util.Calendar.HOUR_OF_DAY)
        val minute = now.get(java.util.Calendar.MINUTE)
        val currentX = hour + minute / 60f // 例：14時35分→14.583
        val nowStr = "%02d:%02d".format(hour, minute)

        val dataSet = mChart.lineData.getDataSetByIndex(0)
        val lowerEntry = dataSet.getEntryForXValue(currentX, Float.NaN, DataSet.Rounding.DOWN)
        val upperEntry = dataSet.getEntryForXValue(currentX, Float.NaN, DataSet.Rounding.UP)

        val currentY = if (lowerEntry != null && upperEntry != null && lowerEntry.x != upperEntry.x) {
            // 線形補間
            val ratio = (currentX - lowerEntry.x) / (upperEntry.x - lowerEntry.x)
            lowerEntry.y + (upperEntry.y - lowerEntry.y) * ratio
        } else {
            lowerEntry?.y ?: upperEntry?.y ?: 0f
        }


        val transformer = mChart.getTransformer(dataSet.axisDependency)
        val pos = transformer.getPixelForValues(currentX, currentY)

        val chartWidth = mViewPortHandler.chartWidth
        val bubbleWidth = 120f
        val bubbleHeight = 60f
        var bubbleX = pos.x.toFloat() - bubbleWidth / 2
        val bubbleY = pos.y.toFloat() - bubbleHeight - 30f

// 左端
        if (bubbleX < 0) bubbleX = 0f
// 右端
        if (bubbleX + bubbleWidth > chartWidth) bubbleX = chartWidth - bubbleWidth

// 吹き出し
        c.drawRoundRect(bubbleX, bubbleY, bubbleX + bubbleWidth, bubbleY + bubbleHeight, 24f, 24f, bubblePaint)
        c.drawText(nowStr, bubbleX + 20f, bubbleY + bubbleHeight / 2 + 12f, textPaint)
// ポインタ
        val pointerX = pos.x.coerceIn(bubbleX.toDouble() + 10, bubbleX + bubbleWidth.toDouble() - 10)
        val path = android.graphics.Path().apply {
            moveTo(pointerX.toFloat() - 10f, bubbleY + bubbleHeight)
            lineTo(pointerX.toFloat() + 10f, bubbleY + bubbleHeight)
            lineTo(pos.x.toFloat(), bubbleY + bubbleHeight + 20f)
            close()
        }
        c.drawPath(path, bubblePaint)



    }
}
