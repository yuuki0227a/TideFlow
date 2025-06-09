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

    var showBubble: Boolean = false // 吹き出しの表示非表示フラグ

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
    override fun drawExtras(c: Canvas) {
        // 既存の追加描画処理も実施
        super.drawExtras(c)
        // 吹き出しの表示非表示
        if (!showBubble) return

        // LineChartのデータが取得できない場合は何もしない
        val data = mChart.lineData ?: return

        // 現在時刻を取得（24h制の時・分）
        val now = java.util.Calendar.getInstance()
        val hour = now.get(java.util.Calendar.HOUR_OF_DAY)
        val minute = now.get(java.util.Calendar.MINUTE)
        // 小数値（例: 14時35分→14.583）
        val currentX = hour + minute / 60f
        // 表示用の文字列（例: "14:35"）
        val nowStr = "%02d:%02d".format(hour, minute)

        // 1つめのデータセットを対象とする（通常は0番目）
        val dataSet = mChart.lineData.getDataSetByIndex(0)

        // 現在時刻に最も近い「前後」のエントリを取得
        val lowerEntry = dataSet.getEntryForXValue(currentX, Float.NaN, DataSet.Rounding.DOWN)
        val upperEntry = dataSet.getEntryForXValue(currentX, Float.NaN, DataSet.Rounding.UP)

        // Y座標の補間計算
        // 2点間を線形補間で求める。どちらかしかなければその値。両方なければ0。
        val currentY = if (lowerEntry != null && upperEntry != null && lowerEntry.x != upperEntry.x) {
            val ratio = (currentX - lowerEntry.x) / (upperEntry.x - lowerEntry.x)
            lowerEntry.y + (upperEntry.y - lowerEntry.y) * ratio
        } else {
            lowerEntry?.y ?: upperEntry?.y ?: 0f
        }

        // グラフ上のデータ値からピクセル座標へ変換
        val transformer = mChart.getTransformer(dataSet.axisDependency)
        val pos = transformer.getPixelForValues(currentX, currentY)

        // バブル（吹き出し）の表示領域調整用
        val chartWidth = mViewPortHandler.chartWidth
        val bubbleWidth = 120f
        val bubbleHeight = 60f
        // バブルの左上座標
        var bubbleX = pos.x.toFloat() - bubbleWidth / 2
        val bubbleY = pos.y.toFloat() - bubbleHeight - 205f // バブルは点の少し上

        // 左端にはみ出し防止
        if (bubbleX < 0) bubbleX = 0f
        // 右端にはみ出し防止
        if (bubbleX + bubbleWidth > chartWidth) bubbleX = chartWidth - bubbleWidth

        // バブル本体（角丸四角）の描画
        c.drawRoundRect(
            bubbleX, bubbleY,
            bubbleX + bubbleWidth, bubbleY + bubbleHeight,
            24f, 24f, bubblePaint
        )
        // バブル内に現在時刻をテキスト表示
        c.drawText(
            nowStr,
            bubbleX + 20f,
            bubbleY + bubbleHeight / 2 + 12f, // テキストの縦位置調整
            textPaint
        )

        // バブル下部からデータポイントへの「吹き出しポインタ」（点線）の描画
        val pointerX = pos.x.coerceIn(bubbleX.toDouble() + 10, bubbleX + bubbleWidth.toDouble() - 10)
        // ポインタ（点線）の開始座標（バブルの下中央）
        val pointerStartX = pointerX.toFloat()
        val pointerStartY = bubbleY + bubbleHeight
        // ポインタ（点線）の終点座標（グラフ点方向へ100px下）
        val pointerEndX = pos.x.toFloat()
        val pointerEndY = bubbleY + bubbleHeight + 200f

        // まっすぐな直線の点線
        val straightPath = android.graphics.Path().apply {
            moveTo(pointerStartX, pointerStartY)
            lineTo(pointerEndX, pointerEndY)
        }
        c.drawPath(straightPath, dottedLinePaint)
    }
}
