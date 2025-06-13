package com.lazyapps.tideflow

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.DashPathEffect
import android.graphics.Paint
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.renderer.LineChartRenderer
import com.github.mikephil.charting.animation.ChartAnimator
import com.github.mikephil.charting.data.DataSet
import com.github.mikephil.charting.utils.ViewPortHandler

/**
 * LineChartに現在時刻の位置を「吹き出し（バブル）」＋時刻文字列で可視化するカスタムレンダラー
 */
class CustomLineChartRenderer(
    chart: LineChart,
    animator: ChartAnimator,
    viewPortHandler: ViewPortHandler
) : LineChartRenderer(chart, animator, viewPortHandler) {

    var mIsShowBubble: Boolean = false // 吹き出しの表示非表示フラグ

    // 吹き出し（バブル）の描画用ペイント（白＋シャドウ）
    private val bubblePaint = Paint().apply {
        color = Color.WHITE
        style = Paint.Style.FILL
        setShadowLayer(8f, 0f, 2f, Color.LTGRAY)
    }

    // 吹き出し内のテキスト描画用ペイント（黒・アンチエイリアス）
    private val textPaint = Paint().apply {
        color = Color.BLACK
        textSize = 32f
        isAntiAlias = true
    }

    // 吹き出しポインタ部分（点線）のペイント
    private val dottedLinePaint = Paint().apply {
        color = Color.WHITE
        style = Paint.Style.STROKE
        strokeWidth = 6f
        isAntiAlias = true
        // ←下記を削除またはコメントアウト
        // pathEffect = DashPathEffect(floatArrayOf(18f, 18f), 0f)
    }


    /**
     * グラフの追加要素（現在時刻バブル）の描画
     */
    // 縦線＆ラベル描画用ペイント
    private val verticalLinePaint = Paint().apply {
        color = Color.GRAY
        strokeWidth = 2f
        style = Paint.Style.STROKE
        pathEffect = DashPathEffect(floatArrayOf(10f, 10f), 0f)
        isAntiAlias = true
    }
    private val labelPaint = Paint().apply {
        color = Color.LTGRAY
        textSize = 32f
        textAlign = Paint.Align.CENTER
        isAntiAlias = true
    }

    override fun drawExtras(c: Canvas) {
        super.drawExtras(c)
        // X軸ラベルと縦線
        drawCustomXAxisLabelsAndLines(c)
        // 吹き出し
        if (mIsShowBubble) {
            drawCurrentTimeBubble(c)
        }
    }

    /** 現在時刻の吹き出し＋バブルを描画 */
    private fun drawCurrentTimeBubble(c: Canvas) {
        val data = mChart.lineData ?: return

        val now = java.util.Calendar.getInstance()
        val hour = now.get(java.util.Calendar.HOUR_OF_DAY)
        val minute = now.get(java.util.Calendar.MINUTE)
        val currentX = hour + minute / 60f
        val nowStr = "%02d:%02d".format(hour, minute)

        val dataSet = mChart.lineData.getDataSetByIndex(0)
        val lowerEntry = dataSet.getEntryForXValue(currentX, Float.NaN, DataSet.Rounding.DOWN)
        val upperEntry = dataSet.getEntryForXValue(currentX, Float.NaN, DataSet.Rounding.UP)
        val currentY = if (lowerEntry != null && upperEntry != null && lowerEntry.x != upperEntry.x) {
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
        val bubbleY = pos.y.toFloat() - bubbleHeight - 205f

        if (bubbleX < 0) bubbleX = 0f
        if (bubbleX + bubbleWidth > chartWidth) bubbleX = chartWidth - bubbleWidth

        c.drawRoundRect(
            bubbleX, bubbleY,
            bubbleX + bubbleWidth, bubbleY + bubbleHeight,
            24f, 24f, bubblePaint
        )
        c.drawText(
            nowStr,
            bubbleX + 20f,
            bubbleY + bubbleHeight / 2 + 12f,
            textPaint
        )

        val pointerX = pos.x.coerceIn(bubbleX.toDouble() + 10, bubbleX + bubbleWidth.toDouble() - 10)
        val pointerStartX = pointerX.toFloat()
        val pointerStartY = bubbleY + bubbleHeight
        val pointerEndX = pos.x.toFloat()
        val pointerEndY = bubbleY + bubbleHeight + 200f
        val straightPath = android.graphics.Path().apply {
            moveTo(pointerStartX, pointerStartY)
            lineTo(pointerEndX, pointerEndY)
        }
        c.drawPath(straightPath, dottedLinePaint)
    }

    /** X軸のカスタムラベル＆縦線（点線）を描画 */
    private fun drawCustomXAxisLabelsAndLines(c: Canvas) {
        // X軸のラベルを表示したい時刻
        val xValues = listOf(0f, 6f, 12f, 18f, 24f)
        // データセットがない場合は左軸で座標変換
        val dataSet = mChart.lineData?.getDataSetByIndex(0)
        val transformer = if (dataSet != null) mChart.getTransformer(dataSet.axisDependency)
        else mChart.getTransformer(com.github.mikephil.charting.components.YAxis.AxisDependency.LEFT)
        val contentRect = mViewPortHandler.contentRect
        val yTop = contentRect.top
        val yBottom = contentRect.bottom

        for (x in xValues) {
            val xPixel = transformer.getPixelForValues(x, 0f).x.toFloat()
            c.drawLine(xPixel, yTop, xPixel, yBottom, verticalLinePaint)
            c.drawText(
                x.toInt().toString(),
                xPixel,
                yBottom + 40f,
                labelPaint
            )
        }
    }
}
