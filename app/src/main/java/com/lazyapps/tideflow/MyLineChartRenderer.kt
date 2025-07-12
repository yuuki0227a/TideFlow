package com.lazyapps.tideflow

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.DashPathEffect
import android.graphics.Paint
import android.graphics.Typeface
import androidx.core.content.ContextCompat
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.renderer.LineChartRenderer
import com.github.mikephil.charting.animation.ChartAnimator
import com.github.mikephil.charting.data.DataSet
import com.github.mikephil.charting.utils.ViewPortHandler
import kotlin.coroutines.coroutineContext

/**
 * LineChartに現在時刻の位置を「吹き出し（バブル）」＋時刻文字列で可視化するカスタムレンダラー
 */
class CustomLineChartRenderer(
    chart: LineChart,
    animator: ChartAnimator,
    viewPortHandler: ViewPortHandler,
    private val highTideTimes: ArrayList<Triple<Int, Int, Int>>,
    private val lowTideTimes: ArrayList<Triple<Int, Int, Int>>,
    private val afterTideFlowData: TideFlowManager.TideFlowData?,
    private val mContext: Context
) : LineChartRenderer(chart, animator, viewPortHandler) {

    var mIsShowBubble: Boolean = false // 吹き出しの表示非表示フラグ
    var mIsShowDrawTideTimeLabels: Boolean = false // 吹き出しの表示非表示フラグ

    // 吹き出し（バブル）の描画用ペイント（白＋シャドウ）
    private val bubblePaint = Paint().apply {
        color = Color.argb(255,57, 141, 222)
//        color = Color.argb(255,77, 161, 242)
        style = Paint.Style.FILL
        setShadowLayer(8f, 0f, 2f, Color.LTGRAY)
    }

    // 吹き出し内のテキスト描画用ペイント（黒・アンチエイリアス）
    private val textPaint = Paint().apply {
        color = Color.WHITE
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
        textSize = 48f
        textAlign = Paint.Align.CENTER
        isAntiAlias = true
    }

    override fun drawExtras(c: Canvas) {
        super.drawExtras(c)
        // X軸ラベルと縦線
        drawCustomXAxisLabelsAndLines(c)
        // 満潮干潮の時刻表示
        if(mIsShowDrawTideTimeLabels){
            drawTideTimeLabels(c)
            // 満潮干潮時刻にアイコン表示
            drawTideIcons(c)
        }
        // 吹き出し
        if (mIsShowBubble) {
            drawCurrentTimeBubble(c)
        }
    }

    /** 現在時刻の吹き出し＋バブルを描画 */
    private fun drawCurrentTimeBubble(c: Canvas) {
        val data = mChart.lineData ?: return  // グラフデータがなければ描画せず終了

        // 現在時刻を取得
        val now = java.util.Calendar.getInstance()
        val hour = now.get(java.util.Calendar.HOUR_OF_DAY)
        val minute = now.get(java.util.Calendar.MINUTE)
        val currentX = hour + minute / 60f  // 現在時刻をX軸の小数値に変換（例: 13.5）
        val nowStr = "%02d:%02d".format(hour, minute)  // 表示用の時刻文字列（"HH:mm"）

        // 文字を太字に
        textPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        textPaint.textSize = 40f
//        textPaint.textAlign = Paint.Align.LEFT

        // 現在時刻の前後のデータ点を取得
        val dataSet = mChart.lineData.getDataSetByIndex(0)
        val lowerEntry = dataSet.getEntryForXValue(currentX, Float.NaN, DataSet.Rounding.DOWN)
        val upperEntry = dataSet.getEntryForXValue(currentX, Float.NaN, DataSet.Rounding.UP)

        // 現在時刻におけるY値を線形補間で計算
        val currentY = if (lowerEntry != null && upperEntry != null && lowerEntry.x != upperEntry.x) {
            val ratio = (currentX - lowerEntry.x) / (upperEntry.x - lowerEntry.x)
            lowerEntry.y + (upperEntry.y - lowerEntry.y) * ratio
        } else {
            lowerEntry?.y ?: upperEntry?.y ?: 0f  // 該当データがなければ0を使用
        }

        // データ値をピクセル座標に変換
        val transformer = mChart.getTransformer(dataSet.axisDependency)
        val pos = transformer.getPixelForValues(currentX, currentY)

        // バブルのサイズと位置を決定
        val chartWidth = mViewPortHandler.chartWidth
        val bubbleWidth = 130f
        val bubbleHeight = 70f
        var bubbleX = pos.x.toFloat() - bubbleWidth / 2  // バブルをX軸中央に合わせる
        var bubbleY = pos.y.toFloat() - bubbleHeight - 205f  // Y軸位置を少し上にずらす

        // バブルがグラフ外にはみ出さないように制限
        if (bubbleX < 0) bubbleX = 0f
        if (bubbleX + bubbleWidth > chartWidth) bubbleX = chartWidth - bubbleWidth

        // グラフの上端を取得して、吹き出しがはみ出さないよう制限
        val contentTop = mViewPortHandler.contentTop()
        if (bubbleY < contentTop) {
            bubbleY = contentTop
        }

        // 吹き出しの角丸四角形を描画
        c.drawRoundRect(
            bubbleX, bubbleY,
            bubbleX + bubbleWidth, bubbleY + bubbleHeight,
            24f, 24f, bubblePaint
        )

        // 現在時刻の文字列を描画
        c.drawText(
            nowStr,
            bubbleX + 15f,
            bubbleY + bubbleHeight / 2 + 14f,
            textPaint
        )

        // 枠線用 Paint を用意
        val bubbleStrokePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE
            strokeWidth = 3f
            color = Color.WHITE
        }
        // 吹き出し塗り
//        c.drawRoundRect(bubbleX, bubbleY, bubbleX + bubbleWidth, bubbleY + bubbleHeight, 24f, 24f, bubblePaint)

        // 吹き出し枠線
        c.drawRoundRect(bubbleX, bubbleY, bubbleX + bubbleWidth, bubbleY + bubbleHeight, 24f, 24f, bubbleStrokePaint)

        val pointerX = pos.x.coerceIn(bubbleX.toDouble() + 10, bubbleX + bubbleWidth.toDouble() - 10)
        val pointerStartX = pointerX.toFloat()
        val pointerStartY = bubbleY + bubbleHeight
        val rawEndY = bubbleY + bubbleHeight + 200f
        val pointerEndY = rawEndY.coerceAtMost(pos.y.toFloat())  // データ点のY座標より下にはしない
        val pointerEndYClamped = pointerEndY.coerceAtLeast(contentTop)  // グラフの上端より上にはしない

        val pointerEndX = pos.x.toFloat()

        val straightPath = android.graphics.Path().apply {
            moveTo(pointerStartX, pointerStartY)
            lineTo(pointerEndX, pointerEndYClamped)
        }
        c.drawPath(straightPath, dottedLinePaint)
    }


    /** X軸のカスタムラベル＆縦線（点線）を描画 */
    private fun drawCustomXAxisLabelsAndLines(c: Canvas) {
        // X軸のラベルを表示したい時刻
        val xValues = when(afterTideFlowData){
            null -> listOf(0f, 6f, 12f, 18f, 23f)
            else -> listOf(0f, 6f, 12f, 18f, 24f)
        }
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
                yBottom + 50f,
                labelPaint
            )
        }
    }

    /**
     * グラフ上に満潮・干潮の時刻ラベルを描画する
     */
    /** 満潮（highTideTimes）・干潮（lowTideTimes）の時刻リスト（例: listOf("6:41", "21:16")） */
//    var highTideTimes: List<String> = listOf("6:00", "13:00")
//    var lowTideTimes: List<String> = listOf("10:00", "16:00")
    @SuppressLint("DefaultLocale")
    private fun drawTideTimeLabels(c: Canvas) {
        // Paint
        val tideLabelPaint = Paint().apply {
            color = Color.WHITE
            textSize = 52f
            textAlign = Paint.Align.CENTER
            isAntiAlias = true
            setShadowLayer(8f, 0f, 2f, Color.BLACK)
            typeface = android.graphics.Typeface.DEFAULT_BOLD
        }

        // 満潮時刻のリスト作成
        val highTideTimeList: ArrayList<String> = arrayListOf()
        for(highTideTime in highTideTimes){
            if(highTideTime.third == 999) continue
            highTideTimeList.add(
                String.format(
                    "%2d:%02d",
                    highTideTime.first,
                    highTideTime.second
                )
            )
        }
        // 干潮時刻のリスト作成
        val lowTideTimeList: ArrayList<String> = arrayListOf()
        for(lowTideTime in lowTideTimes){
            if(lowTideTime.third == 999) continue
            lowTideTimeList.add(
                String.format(
                    "%2d:%02d",
                    lowTideTime.first,
                    lowTideTime.second
                )
            )
        }

        // 満潮（上にオフセット）
        for (time in highTideTimeList) {
            // 例："6:41"→6.68f などX座標変換
            val hourMin = time.split(":")
            if (hourMin.size != 2) continue
            val x = hourMin[0].toFloatOrNull() ?: continue
            val y = hourMin[1].toFloatOrNull() ?: 0f
            val xValue = x + y / 60f

            // データセットからY値取得（または潮位情報を直接渡してもOK）
            val dataSet = mChart.lineData?.getDataSetByIndex(0) ?: continue
            val entry = dataSet.getEntryForXValue(xValue, Float.NaN, DataSet.Rounding.CLOSEST) ?: continue
            val transformer = mChart.getTransformer(dataSet.axisDependency)
            val pos = transformer.getPixelForValues(xValue, entry.y)

            // 上方向へオフセット（例：-24f）
            c.drawText(time, pos.x.toFloat() - 5f, pos.y.toFloat() - 40f, tideLabelPaint)
        }

        // 干潮（下にオフセット）
        for (time in lowTideTimeList) {
            val hourMin = time.split(":")
            if (hourMin.size != 2) continue
            val x = hourMin[0].toFloatOrNull() ?: continue
            val y = hourMin[1].toFloatOrNull() ?: 0f
            val xValue = x + y / 60f

            val dataSet = mChart.lineData?.getDataSetByIndex(0) ?: continue
            val entry = dataSet.getEntryForXValue(xValue, Float.NaN, DataSet.Rounding.CLOSEST) ?: continue
            val transformer = mChart.getTransformer(dataSet.axisDependency)
            val pos = transformer.getPixelForValues(xValue, entry.y)

            // 下方向へオフセット（例：+44f）
            c.drawText(time, pos.x.toFloat() - 5f, pos.y.toFloat() + 78f, tideLabelPaint)
        }
    }

    private val highTideIcon: Bitmap by lazy {
        drawableToBitmap(mContext, R.drawable.data_entry_icon_high, 40)
    }

    private val lowTideIcon: Bitmap by lazy {
        drawableToBitmap(mContext, R.drawable.data_entry_icon_low, 40)
    }
    private fun drawTideIcons(c: Canvas) {
        val dataSet = mChart.lineData?.getDataSetByIndex(0) ?: return

        // 満潮アイコン描画
        for (time in highTideTimes) {
            if (time.third == 999) continue
            val x = time.first + time.second / 60f
            val y = time.third.toFloat()
            val pos = mChart.getTransformer(dataSet.axisDependency).getPixelForValues(x, y)
            val iconWidth = highTideIcon.width
            val iconHeight = highTideIcon.height
            val left = pos.x.toFloat() - iconWidth / 2f
            val top = pos.y.toFloat() - iconHeight /2f
            c.drawBitmap(highTideIcon, left, top, null)
        }

        // 干潮アイコン描画
        for (time in lowTideTimes) {
            if (time.third == 999) continue
            val x = time.first + time.second / 60f
            val y = time.third.toFloat()
            val pos = mChart.getTransformer(dataSet.axisDependency).getPixelForValues(x, y)
            val iconWidth = lowTideIcon.width
            val iconHeight = lowTideIcon.height
            val left = pos.x.toFloat() - iconWidth / 2f
            val top = pos.y.toFloat() - iconHeight /2f
            c.drawBitmap(lowTideIcon, left, top, null)
        }
    }
    private fun drawableToBitmap(context: Context, drawableId: Int, sizePx: Int): Bitmap {
        val drawable = ContextCompat.getDrawable(context, drawableId)!!
        val bmp = Bitmap.createBitmap(sizePx, sizePx, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bmp)
        drawable.setBounds(0, 0, sizePx, sizePx)
        drawable.draw(canvas)
        return bmp
    }



}
