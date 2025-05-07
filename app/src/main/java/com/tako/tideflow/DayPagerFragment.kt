package com.tako.tideflow

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.github.mikephil.charting.components.AxisBase
import com.tako.tideflow.databinding.FragmentDayPagerBinding
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.listener.ChartTouchListener
import com.github.mikephil.charting.listener.OnChartGestureListener
import com.github.mikephil.charting.utils.MPPointF
import java.time.LocalDateTime
import java.util.Calendar

class DayPagerFragment : Fragment() {
    private lateinit var mBinding: FragmentDayPagerBinding
    private lateinit var mContext: Context

    private lateinit var tideFlowData: TideFlowManager.TideFlowData
    private lateinit var locationMap: MutableMap<String, String>

    companion object{
        const val DAY_PAGER_HIGH_TIDE_TIMES_TIME_FORMAT = "%2d:%02d"
        const val DAY_PAGER_HIGH_TIDE_TIMES_TIDE_LEVEL_FORMAT = "%3d\tcm"

        private const val ARG_TIDE_DATA = "arg_tide_data"
        private const val ARG_LOCATION_MAP = "arg_location_map"

        fun newInstance(data: TideFlowManager.TideFlowData, locationMap: HashMap<String, String>): DayPagerFragment {
            return DayPagerFragment().apply {
                arguments = Bundle().apply {
                    putSerializable("tideFlowData", data)
                    putSerializable("locationMap", locationMap)
                }
            }
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            tideFlowData = (arguments?.getSerializable("tideFlowData") as? TideFlowManager.TideFlowData)!!
            locationMap = (arguments?.getSerializable("locationMap") as? HashMap<String, String>)!!
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentDayPagerBinding.inflate(inflater, container, false)
        mContext = mBinding.root.context
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        /* 月画像、月齢、潮状態の表示 */
        // threeten.bpのLocalDateの取得
        val bpLocalDate = org.threeten.bp.LocalDate.of(tideFlowData.tideDate.first, tideFlowData.tideDate.second, tideFlowData.tideDate.third)
        // 月齢の取得
        val moonAge = Util.getMoonAge(bpLocalDate)
        // 少数第一位以下を切り捨てる。
        val roundedMoonAge = String.format("%.1f", moonAge).toDouble()
        // 月齢から潮情報を取得する。
        val tideCondition = Util.getTideInfoFromLunarPhase(moonAge)
        // 月齢表示
        mBinding.dayPagerMoonAgeTextView.text = String.format("月齢 %.1f", roundedMoonAge)
//        mBinding.dayPagerMoonAgeTextView.text = String.format("%.1f", roundedMoonAge)
        // 潮状態表示
        mBinding.dayPagerTideConditionTextView.text = tideCondition
        // 潮状態ごとに色分けする。
        if(tideCondition != null){
            Util.setColorForTideType(mContext, mBinding.dayPagerTideConditionTextView, tideCondition)
        }
        // 月画像
        when(moonAge.toInt()){
            0 -> mBinding.dayPagerMoonImageView.setImageResource(R.drawable.moon_00)
            1 -> mBinding.dayPagerMoonImageView.setImageResource(R.drawable.moon_01)
            2 -> mBinding.dayPagerMoonImageView.setImageResource(R.drawable.moon_02)
            3 -> mBinding.dayPagerMoonImageView.setImageResource(R.drawable.moon_03)
            4 -> mBinding.dayPagerMoonImageView.setImageResource(R.drawable.moon_04)
            5 -> mBinding.dayPagerMoonImageView.setImageResource(R.drawable.moon_05)
            6 -> mBinding.dayPagerMoonImageView.setImageResource(R.drawable.moon_06)
            7 -> mBinding.dayPagerMoonImageView.setImageResource(R.drawable.moon_07)
            8 -> mBinding.dayPagerMoonImageView.setImageResource(R.drawable.moon_08)
            9 -> mBinding.dayPagerMoonImageView.setImageResource(R.drawable.moon_09)
            10 -> mBinding.dayPagerMoonImageView.setImageResource(R.drawable.moon_10)
            11 -> mBinding.dayPagerMoonImageView.setImageResource(R.drawable.moon_11)
            12 -> mBinding.dayPagerMoonImageView.setImageResource(R.drawable.moon_12)
            13 -> mBinding.dayPagerMoonImageView.setImageResource(R.drawable.moon_13)
            14 -> mBinding.dayPagerMoonImageView.setImageResource(R.drawable.moon_14)
            15 -> mBinding.dayPagerMoonImageView.setImageResource(R.drawable.moon_15)
            16 -> mBinding.dayPagerMoonImageView.setImageResource(R.drawable.moon_16)
            17 -> mBinding.dayPagerMoonImageView.setImageResource(R.drawable.moon_17)
            18 -> mBinding.dayPagerMoonImageView.setImageResource(R.drawable.moon_18)
            19 -> mBinding.dayPagerMoonImageView.setImageResource(R.drawable.moon_19)
            20 -> mBinding.dayPagerMoonImageView.setImageResource(R.drawable.moon_20)
            21 -> mBinding.dayPagerMoonImageView.setImageResource(R.drawable.moon_21)
            22 -> mBinding.dayPagerMoonImageView.setImageResource(R.drawable.moon_22)
            23 -> mBinding.dayPagerMoonImageView.setImageResource(R.drawable.moon_23)
            24 -> mBinding.dayPagerMoonImageView.setImageResource(R.drawable.moon_24)
            25 -> mBinding.dayPagerMoonImageView.setImageResource(R.drawable.moon_25)
            26 -> mBinding.dayPagerMoonImageView.setImageResource(R.drawable.moon_26)
            27 -> mBinding.dayPagerMoonImageView.setImageResource(R.drawable.moon_27)
            28 -> mBinding.dayPagerMoonImageView.setImageResource(R.drawable.moon_28)
            29 -> mBinding.dayPagerMoonImageView.setImageResource(R.drawable.moon_29)
            else -> mBinding.dayPagerMoonImageView.setImageResource(R.drawable.moon_00)
        }

        /* 満潮 */
        // 1
        if(tideFlowData.highTideTimes[0].third != 999){
            mBinding.dayPagerHighTideTimesTime1.text = String.format(
                DAY_PAGER_HIGH_TIDE_TIMES_TIME_FORMAT, tideFlowData.highTideTimes[0].first, tideFlowData.highTideTimes[0].second,
            )
            mBinding.dayPagerHighTideTimesTideLevel1.text = String.format(
                DAY_PAGER_HIGH_TIDE_TIMES_TIDE_LEVEL_FORMAT, tideFlowData.highTideTimes[0].third,
            )
        }
        // 2
        if(tideFlowData.highTideTimes[1].third != 999){
            mBinding.dayPagerHighTideTimesTime2.text = String.format(
                DAY_PAGER_HIGH_TIDE_TIMES_TIME_FORMAT, tideFlowData.highTideTimes[1].first, tideFlowData.highTideTimes[1].second,
            )
            mBinding.dayPagerHighTideTimesTideLevel2.text = String.format(
                DAY_PAGER_HIGH_TIDE_TIMES_TIDE_LEVEL_FORMAT, tideFlowData.highTideTimes[1].third,
            )
        }
        // 3
        if(tideFlowData.highTideTimes[2].third != 999){
            mBinding.dayPagerHighTideTimesTime3.text = String.format(
                DAY_PAGER_HIGH_TIDE_TIMES_TIME_FORMAT, tideFlowData.highTideTimes[2].first, tideFlowData.highTideTimes[2].second,
            )
            mBinding.dayPagerHighTideTimesTideLevel3.text = String.format(
                DAY_PAGER_HIGH_TIDE_TIMES_TIDE_LEVEL_FORMAT, tideFlowData.highTideTimes[2].third,
            )
        }
        // 4
        if(tideFlowData.highTideTimes[3].third != 999){
            mBinding.dayPagerHighTideTimesTime4.text = String.format(
                DAY_PAGER_HIGH_TIDE_TIMES_TIME_FORMAT, tideFlowData.highTideTimes[3].first, tideFlowData.highTideTimes[3].second,
            )
            mBinding.dayPagerHighTideTimesTideLevel4.text = String.format(
                DAY_PAGER_HIGH_TIDE_TIMES_TIDE_LEVEL_FORMAT, tideFlowData.highTideTimes[3].third,
            )
        }

        /* 干潮 */
        // 1
        if(tideFlowData.lowTideTimes[0].third != 999){
            mBinding.dayPagerLowTideTimesTime1.text = String.format(
                DAY_PAGER_HIGH_TIDE_TIMES_TIME_FORMAT, tideFlowData.lowTideTimes[0].first, tideFlowData.lowTideTimes[0].second,
            )
            mBinding.dayPagerLowTideTimesTideLevel1.text = String.format(
                DAY_PAGER_HIGH_TIDE_TIMES_TIDE_LEVEL_FORMAT, tideFlowData.lowTideTimes[0].third,
            )
        }
        // 2
        if(tideFlowData.lowTideTimes[1].third != 999){
            mBinding.dayPagerLowTideTimesTime2.text = String.format(
                DAY_PAGER_HIGH_TIDE_TIMES_TIME_FORMAT, tideFlowData.lowTideTimes[1].first, tideFlowData.lowTideTimes[1].second,
            )
            mBinding.dayPagerLowTideTimesTideLevel2.text = String.format(
                DAY_PAGER_HIGH_TIDE_TIMES_TIDE_LEVEL_FORMAT, tideFlowData.lowTideTimes[1].third,
            )
        }
        // 3
        if(tideFlowData.lowTideTimes[2].third != 999){
            mBinding.dayPagerLowTideTimesTime3.text = String.format(
                DAY_PAGER_HIGH_TIDE_TIMES_TIME_FORMAT, tideFlowData.lowTideTimes[2].first, tideFlowData.lowTideTimes[2].second,
            )
            mBinding.dayPagerLowTideTimesTideLevel3.text = String.format(
                DAY_PAGER_HIGH_TIDE_TIMES_TIDE_LEVEL_FORMAT, tideFlowData.lowTideTimes[2].third,
            )
        }
        // 4
        if(tideFlowData.lowTideTimes[3].third != 999){
            mBinding.dayPagerLowTideTimesTime4.text = String.format(
                DAY_PAGER_HIGH_TIDE_TIMES_TIME_FORMAT, tideFlowData.lowTideTimes[3].first, tideFlowData.lowTideTimes[3].second,
            )
            mBinding.dayPagerLowTideTimesTideLevel4.text = String.format(
                DAY_PAGER_HIGH_TIDE_TIMES_TIDE_LEVEL_FORMAT, tideFlowData.lowTideTimes[3].third,
            )
        }

        /* １時間毎の潮汐データの折線グラフ */
        createLineChart()
    }

    /**
     * １時間毎の潮汐データの折線グラフを作成する。
     * */
    private fun createLineChart(){
        /* テーマに基づいてカラーを取得 */
        // テキストカラー
        val themeTextColor: Int = ContextCompat.getColor(
            mContext,
            R.color.line_chart_data_point_text_color
        )
        // グラフカラー
        val themeGraphColor: Int = ContextCompat.getColor(
            mContext,
            R.color.line_chart_graph_color
        )
        // １時間ごとの潮位
        val hourlyTideLevelList: ArrayList<Float> = arrayListOf()
        for(hourlyTideLevel in tideFlowData.hourlyTideLevels){
            hourlyTideLevelList.add(hourlyTideLevel.toFloat())
        }
        // ③元データをEntry型に変換したリストを準備
        val dataEntries = mutableListOf<Entry>()
        hourlyTideLevelList.forEachIndexed { index, value ->
            // X軸は配列のインデックス番号
            val dataEntry = Entry(index.toFloat(), value)
            dataEntry.icon = ContextCompat.getDrawable(mContext, R.drawable.data_entry_icon_other)
            dataEntries.add(dataEntry)
        }
        // ④グラフ線やポインタなどの機能、デザインなどを設定
        val lineDataSet = LineDataSet(dataEntries, "グラフ名")

        // ⑤ラベルやグラフなどの機能、デザインなどを設定
        // x軸のラベルをbottomに表示
        mBinding.dayPagerLineChart.xAxis.position = XAxis.XAxisPosition.BOTTOM

        // ⑥グラフのデータに格納
        mBinding.dayPagerLineChart.data = LineData(lineDataSet)

        /* x軸ラベルの作成 */
        // x軸のラベル
        val quarterList: ArrayList<String> = arrayListOf()
        // 0～23を追加する。
        val xTimeMax = 24
        for((xTime, _) in (0 until xTimeMax).withIndex()){
            quarterList.add("$xTime")
        }
        // x軸の項目名を設定
        val formatter: ValueFormatter = object : ValueFormatter() {
            override fun getAxisLabel(value: Float, axis: AxisBase): String {
                /*
                *   データ削除後の再描画でラベルが要素外まで参照されてしまう
                *   要素外を参照しないように対応。原因は不明。 */
                if (quarterList.size <= value.toInt()) {
                    return "e"
                }
                return quarterList[value.toInt()]
            }
        }
        lineDataSet.valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                // 小数点以下は消す。
                return "%.0f".format(value)
            }
        }

        // 現在の日時
        val currentDateTime = LocalDateTime.now()

        /* グラフデザイン */
        // グラフの線の太さ
        lineDataSet.lineWidth = 5.0f
        // グラフモード(曲線)
        lineDataSet.mode = LineDataSet.Mode.CUBIC_BEZIER
        // グラフの色
        lineDataSet.colors = listOf(themeGraphColor)
        // 点の色
//        lineDataSet.circleColors = listOf(Color.CYAN)
        // 点の大きさ
//        lineDataSet.circleRadius = 0.0f
        // 塗りつぶしカラー
        lineDataSet.fillColor = ContextCompat.getColor(
            mContext,
            R.color.line_chart_fill_color
        )
        // 塗りつぶし
        lineDataSet.setDrawFilled(true)
        // 値非表示
        lineDataSet.setDrawValues(true)
        // ポインタ非表示
        lineDataSet.setDrawCircles(false)
        // 値のテキストカラー
        lineDataSet.valueTextColor = themeTextColor
        // 値のテキストサイズ
        lineDataSet.valueTextSize = 10f
        // アイコンのオフセット
        lineDataSet.iconsOffset = MPPointF(0f,2f)

        /* グラフ設定 */
        // データがない場合のテキスト
        mBinding.dayPagerLineChart.setNoDataText("データがありません")
        // タップでの点の選択を無効
        mBinding.dayPagerLineChart.isHighlightPerTapEnabled = false
        // ピンチでのズームを無効
        mBinding.dayPagerLineChart.setPinchZoom(false)
        // データの最大表示範囲を制限 (データ数が多い場合横にスクロールで表示エリアを移動可能)
//        mBinding.dayPagerLineChart.setVisibleXRangeMaximum(10f) // ※
        // データの最小表示範囲を制限 (データ数が多い場合横にスクロールで表示エリアを移動可能)
//        mBinding.dayPagerLineChart.setVisibleXRangeMinimum(5f) // ※
        // データの表示位置を指定したX軸の値にする(インデックスではなくX軸の値を指定)
        mBinding.dayPagerLineChart.moveViewToX((hourlyTideLevelList.size - 1).toFloat()) // ※

        /* 現日時の処理 */
        // 日付が本日の場合のみ表示する。
        if(currentDateTime.dayOfMonth == tideFlowData.tideDate.third){
            // 選択したX軸の位置をハイライト表示(currentHour: 現時刻にフォーカスさせる)
            val calendar = Calendar.getInstance()
            val currentHour = calendar.get(Calendar.HOUR_OF_DAY)
            // 縦横のターゲットライン
//            mBinding.dayPagerLineChart.highlightValue(currentHour.toFloat(),0) // ※
            /* データポイントの点の変更 */
            // 現時刻の場合
            if(currentDateTime.hour == currentHour){
                // アイコン
                val currentEntry = dataEntries[currentHour]
                currentEntry.icon = ContextCompat.getDrawable(mContext, R.drawable.data_entry_icon_current)
            }
        }

        // 表示されているデータの一番低い値を取得
        mBinding.dayPagerLineChart.lowestVisibleX
        // 表示されているデータの一番高い値を取得
        mBinding.dayPagerLineChart.highestVisibleX
        // ダブルタップでのズームを無効
        mBinding.dayPagerLineChart.isDoubleTapToZoomEnabled = false
        // グラフアニメーション
        mBinding.dayPagerLineChart.animateXY(1, 1)
        // グラフ拡大許可
        mBinding.dayPagerLineChart.setScaleEnabled(false)
        // タッチイベントを無効にする
        mBinding.dayPagerLineChart.setTouchEnabled(false)

        /* ラベルのカスタマイズ */
        // 右下のDescription Labelを非表示
        mBinding.dayPagerLineChart.description.isEnabled = false
        // グラフ名ラベルを非表示
        mBinding.dayPagerLineChart.legend.isEnabled = false
        // Y軸右側ラベルを非表示
        mBinding.dayPagerLineChart.axisRight.isEnabled = false
        // 上からのオフセット
        mBinding.dayPagerLineChart.extraTopOffset = 30f

        /* x軸関連 */
        mBinding.dayPagerLineChart.xAxis.apply {
            // ラベルカラー
            textColor = themeTextColor
            // ラベル表示の数
            labelCount = quarterList.size - 1
            // ラベル挿入
            valueFormatter = formatter
            // ラベルを1.0ずつ増加させる　※これをしないと0.5のときなどに余計なラベルが追加される
            granularity = 1f
            // グリッド表示
            setDrawGridLines(false)
            // ラベル表示
            setDrawLabels(true)
            // 枠線表示
            setDrawAxisLine(true)
        }

        /* Y軸関連(左側) */
        mBinding.dayPagerLineChart.axisLeft.apply {
            // ラベルカラー
            textColor = themeTextColor
            // グリッド表示
            setDrawGridLines(false)
            // ラベル表示
            setDrawLabels(false)
            // 枠線表示
            setDrawAxisLine(false)
            // y軸ラベルの表示個数
            labelCount = 10
            // y左軸最大値
//            axisMaximum = 300f
            // y左軸最小値
//            axisMinimum = 0f
        }

        /* データのポインタを画像(Image)にする */
//        var icon: Drawable? = ResourcesCompat.getDrawable(getResources(), R.drawable.android, null)
//        val dataEntry = Entry(key.toFloat(), value.toFloat(), icon)
        // アイコンを非表示にする
//        lineDataSet.setDrawIcons(false)
        // 表示するデータ値数を指定
//        lineChart.setMaxVisibleValueCount(200)

        /* 画像をリサイズ */
//        val icon: Drawable? = ResourcesCompat.getDrawable(getResources(), R.drawable.myImage, null)
//        val bitmap = (icon as BitmapDrawable).bitmap
//        val resizeDrawable = BitmapDrawable(resources, Bitmap.createScaledBitmap(bitmap, 40, 40, true))
//        val dataEntry = Entry(value.first, value.second, resizeDrawable)

        /* タップした際にデータを取得する */
        // Chartのリスナーを設定します
        mBinding.dayPagerLineChart.setOnChartGestureListener(object : OnChartGestureListener {
            override fun onChartGestureStart(me: MotionEvent?, lastPerformedGesture: ChartTouchListener.ChartGesture?) {
                // タッチジェスチャーが開始された際の処理
            }

            override fun onChartGestureEnd(me: MotionEvent?, lastPerformedGesture: ChartTouchListener.ChartGesture?) {
                // タッチジェスチャーが終了した際の処理
            }

            override fun onChartLongPressed(me: MotionEvent?) {
                // 長押しされた際の処理
            }

            override fun onChartDoubleTapped(me: MotionEvent?) {
                // ダブルタップされた際の処理
            }

            override fun onChartSingleTapped(me: MotionEvent?) {
                // シングルタップされた際の処理
                // タップされた位置にあるデータを取得してみる
                val highlight = mBinding.dayPagerLineChart.getHighlightByTouchPoint(me?.x ?: 0f, me?.y ?: 0f)
                if (highlight != null) {
                    val entry = mBinding.dayPagerLineChart.data.getDataSetByIndex(highlight.dataSetIndex).getEntryForIndex(highlight.x.toInt())
                    Log.e("------", entry.y.toString())
                }
            }

            override fun onChartFling(me1: MotionEvent?, me2: MotionEvent?, velocityX: Float, velocityY: Float) {
                // フリックされた際の処理
            }

            override fun onChartScale(me: MotionEvent?, scaleX: Float, scaleY: Float) {
                // ズームされた際の処理
            }

            override fun onChartTranslate(me: MotionEvent?, dX: Float, dY: Float) {
                // 移動された際の処理
            }
        })

        /* グラフをリセットする */
//        lineChart.data?.clearValues()
//        lineChart.clear()
//        lineChart.notifyDataSetChanged()
//        lineChart.invalidate()
    }
}