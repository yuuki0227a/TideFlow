package com.lazyapps.tideflow

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.lazyapps.tideflow.databinding.FragmentDayPagerBinding
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.listener.ChartTouchListener
import com.github.mikephil.charting.listener.OnChartGestureListener
import com.lazyapps.tideflow.Util.dayOfWeekENtoJP
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.Calendar
import kotlin.text.*

class DayPagerFragment : Fragment() {
    private lateinit var mBinding: FragmentDayPagerBinding
    private lateinit var mContext: Context

    private lateinit var tideFlowData: TideFlowManager.TideFlowData
    private lateinit var locationMap: MutableMap<String, String>
    private var beforeTideFlowData: TideFlowManager.TideFlowData? = null
    private var afterTideFlowData: TideFlowManager.TideFlowData? = null

    private var mTideCondition: String? = ""

    companion object{
        const val DAY_PAGER_HIGH_TIDE_TIMES_TIME_FORMAT = "%2d:%02d"
        const val DAY_PAGER_HIGH_TIDE_TIMES_TIDE_LEVEL_FORMAT = "%3d\tcm"

        private const val ARG_TIDE_DATA = "arg_tide_data"
        private const val ARG_LOCATION_MAP = "arg_location_map"

        fun newInstance(data: TideFlowManager.TideFlowData,
                        locationMap: HashMap<String, String>,
                        beforeData: TideFlowManager.TideFlowData?,
                        afterData: TideFlowManager.TideFlowData?
                        ): DayPagerFragment {
            return DayPagerFragment().apply {
                arguments = Bundle().apply {
                    putSerializable("tideFlowData", data)
                    putSerializable("locationMap", locationMap)
                    putSerializable("beforeTideFlowData", beforeData)
                    putSerializable("afterTideFlowData", afterData)
                }
            }
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            tideFlowData = (arguments?.getSerializable("tideFlowData") as? TideFlowManager.TideFlowData)!!
            locationMap = (arguments?.getSerializable("locationMap") as? HashMap<String, String>)!!
            beforeTideFlowData = arguments?.getSerializable("beforeTideFlowData") as? TideFlowManager.TideFlowData
            afterTideFlowData = arguments?.getSerializable("afterTideFlowData") as? TideFlowManager.TideFlowData
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
        // 日付
        val localDate = LocalDate.of(tideFlowData.tideDate.first, tideFlowData.tideDate.second, tideFlowData.tideDate.third)
        // 曜日
        val dayOfWeek = dayOfWeekENtoJP(localDate.dayOfWeek.toString())
        // 日付表示
        mBinding.dayPagerDateTextView.text = String.format("%4d年%2d月%2d日（%s）", localDate.year, localDate.monthValue, localDate.dayOfMonth, dayOfWeek)
        // threeten.bpのLocalDateの取得
        val bpLocalDate = org.threeten.bp.LocalDate.of(tideFlowData.tideDate.first, tideFlowData.tideDate.second, tideFlowData.tideDate.third)
        // 月齢の取得
        val moonAge = Util.getMoonAge(bpLocalDate)
        // 少数第一位以下を切り捨てる。
        val roundedMoonAge = String.format("%.1f", moonAge).toDouble()
        // 月齢から潮情報を取得する。
        mTideCondition = Util.getTideInfoFromLunarPhase(moonAge)
        val tideCondition = Util.getTideInfoFromLunarPhase(moonAge)
        // 月齢表示
        mBinding.dayPagerMoonAgeTextView.text = String.format("月齢 %.1f", roundedMoonAge)
//        mBinding.dayPagerMoonAgeTextView.text = String.format("%.1f", roundedMoonAge)
        // 潮状態表示
        mBinding.dayPagerTideConditionTextView.text = tideCondition
        // 潮状態ごとに色分けする。
//        if(tideCondition != null){
//            Util.setColorForTideType(mContext, mBinding.dayPagerTideConditionTextView, tideCondition)
//        }
        // 月画像 TODO. 月画像を作成して実装する
        mBinding.dayPagerMoonImageView.setImageResource(R.drawable.moon_debug)
//        when(moonAge.toInt()){
//            0 -> mBinding.dayPagerMoonImageView.setImageResource(R.drawable.moon_00)
//            1 -> mBinding.dayPagerMoonImageView.setImageResource(R.drawable.moon_01)
//            2 -> mBinding.dayPagerMoonImageView.setImageResource(R.drawable.moon_02)
//            3 -> mBinding.dayPagerMoonImageView.setImageResource(R.drawable.moon_03)
//            4 -> mBinding.dayPagerMoonImageView.setImageResource(R.drawable.moon_04)
//            5 -> mBinding.dayPagerMoonImageView.setImageResource(R.drawable.moon_05)
//            6 -> mBinding.dayPagerMoonImageView.setImageResource(R.drawable.moon_06)
//            7 -> mBinding.dayPagerMoonImageView.setImageResource(R.drawable.moon_07)
//            8 -> mBinding.dayPagerMoonImageView.setImageResource(R.drawable.moon_08)
//            9 -> mBinding.dayPagerMoonImageView.setImageResource(R.drawable.moon_09)
//            10 -> mBinding.dayPagerMoonImageView.setImageResource(R.drawable.moon_10)
//            11 -> mBinding.dayPagerMoonImageView.setImageResource(R.drawable.moon_11)
//            12 -> mBinding.dayPagerMoonImageView.setImageResource(R.drawable.moon_12)
//            13 -> mBinding.dayPagerMoonImageView.setImageResource(R.drawable.moon_13)
//            14 -> mBinding.dayPagerMoonImageView.setImageResource(R.drawable.moon_14)
//            15 -> mBinding.dayPagerMoonImageView.setImageResource(R.drawable.moon_15)
//            16 -> mBinding.dayPagerMoonImageView.setImageResource(R.drawable.moon_16)
//            17 -> mBinding.dayPagerMoonImageView.setImageResource(R.drawable.moon_17)
//            18 -> mBinding.dayPagerMoonImageView.setImageResource(R.drawable.moon_18)
//            19 -> mBinding.dayPagerMoonImageView.setImageResource(R.drawable.moon_19)
//            20 -> mBinding.dayPagerMoonImageView.setImageResource(R.drawable.moon_20)
//            21 -> mBinding.dayPagerMoonImageView.setImageResource(R.drawable.moon_21)
//            22 -> mBinding.dayPagerMoonImageView.setImageResource(R.drawable.moon_22)
//            23 -> mBinding.dayPagerMoonImageView.setImageResource(R.drawable.moon_23)
//            24 -> mBinding.dayPagerMoonImageView.setImageResource(R.drawable.moon_24)
//            25 -> mBinding.dayPagerMoonImageView.setImageResource(R.drawable.moon_25)
//            26 -> mBinding.dayPagerMoonImageView.setImageResource(R.drawable.moon_26)
//            27 -> mBinding.dayPagerMoonImageView.setImageResource(R.drawable.moon_27)
//            28 -> mBinding.dayPagerMoonImageView.setImageResource(R.drawable.moon_28)
//            29 -> mBinding.dayPagerMoonImageView.setImageResource(R.drawable.moon_29)
//            else -> mBinding.dayPagerMoonImageView.setImageResource(R.drawable.moon_00)
//        }

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

//        /* １時間毎の潮汐データの折線グラフ */
//        createLineChart()

        // TODO. test
        mBinding.dayPagerMoonImageView.setOnClickListener {
            createLineChart()
        }
        // グラフ作成前に表示される文字列
        mBinding.dayPagerLineChart.setNoDataText("")
    }

    override fun onStart() {
        super.onStart()
        /* １時間毎の潮汐データの折線グラフ */
//        createLineChart()
    }

    override fun onResume() {
        super.onResume()
        createLineChart()
    }

    override fun onPause() {
        super.onPause()
        mBinding.dayPagerLineChart.setNoDataText("")   // NoDataTextを空に
        mBinding.dayPagerLineChart.clear()             // データも全部クリア
        // 吹き出し更新スレッド破棄
        stopRepeatingTask()
        // ふきだしを非表示にする
        if(mCustomLineChartRenderer == null){
            mCustomLineChartRenderer = CustomLineChartRenderer(
                mBinding.dayPagerLineChart,
                mBinding.dayPagerLineChart.animator,
                mBinding.dayPagerLineChart.viewPortHandler,
                tideFlowData.highTideTimes,
                tideFlowData.lowTideTimes,
                afterTideFlowData,
                mContext
            )
        }
        mCustomLineChartRenderer!!.mIsShowBubble = false
        mCustomLineChartRenderer!!.mIsShowDrawTideTimeLabels = false
        mBinding.dayPagerLineChart.renderer = mCustomLineChartRenderer
    }


    private val mHandler = Handler(Looper.getMainLooper())
    private val intervalShowCustomLineChartRenderer: Long = 60_000 // 1分
    private var mCustomLineChartRenderer: CustomLineChartRenderer? = null

    private val runnable = object : Runnable {
        override fun run() {
            // 吹き出しを表示して作成する処理を毎分実行
            mBinding.dayPagerLineChart.renderer = mCustomLineChartRenderer
            mBinding.dayPagerLineChart.invalidate()

            mHandler.postDelayed(this, intervalShowCustomLineChartRenderer)
        }
    }

    private fun startRepeatingAtMinuteZero(isShowBubble: Boolean, isShowDrawTideTimeLabels: Boolean) {
        mCustomLineChartRenderer!!.mIsShowBubble = isShowBubble
        mCustomLineChartRenderer!!.mIsShowDrawTideTimeLabels = isShowDrawTideTimeLabels
        mBinding.dayPagerLineChart.renderer = mCustomLineChartRenderer
        mBinding.dayPagerLineChart.invalidate()
        val currentTime = System.currentTimeMillis()
        val delayToNextMinute = intervalShowCustomLineChartRenderer - (currentTime % intervalShowCustomLineChartRenderer)
        mHandler.postDelayed(runnable, delayToNextMinute)
    }

    private fun stopRepeatingTask() {
        mHandler.removeCallbacks(runnable)
    }

    /**
     * １時間毎の潮汐データの折線グラフを作成する。
     * */
    private fun createLineChart(){
        // １時間ごとの潮位
        val dataEntries = mutableListOf<Entry>()
        // 前日分はEntry.x=-1、当日0〜23時はEntry.x=0〜23、翌日分は24,25
        var hour = -1
        if(beforeTideFlowData != null){
            val dataEntry = Entry(hour.toFloat(), beforeTideFlowData!!.hourlyTideLevels.last().toFloat())
//            dataEntry.icon = ContextCompat.getDrawable(mContext, R.drawable.data_entry_icon_other)
            dataEntries.add(dataEntry)
        }
        hour++
        tideFlowData.hourlyTideLevels.forEach {
            val dataEntry = Entry(hour.toFloat(), it.toFloat())
//            dataEntry.icon = ContextCompat.getDrawable(mContext, R.drawable.data_entry_icon_other)
            dataEntries.add(dataEntry)
            hour++
        }
        if(afterTideFlowData != null){
            val dataEntry = Entry(hour.toFloat(), afterTideFlowData!!.hourlyTideLevels[0].toFloat())
//            dataEntry.icon = ContextCompat.getDrawable(mContext, R.drawable.data_entry_icon_other)
            dataEntries.add(dataEntry)
            hour++
            val dataEntry2 = Entry(hour.toFloat(), afterTideFlowData!!.hourlyTideLevels[1].toFloat())
//            dataEntry2.icon = ContextCompat.getDrawable(mContext, R.drawable.data_entry_icon_other)
            dataEntries.add(dataEntry2)
            hour++
            if(beforeTideFlowData == null){
                val dataEntry3 = Entry(hour.toFloat(), afterTideFlowData!!.hourlyTideLevels[3].toFloat())
//                dataEntry3.icon = ContextCompat.getDrawable(mContext, R.drawable.data_entry_icon_other)
                dataEntries.add(dataEntry3)
                hour++
            }
        }

        // ④グラフ線やポインタなどの機能、デザインなどを設定
        val lineDataSet = LineDataSet(dataEntries, "潮汐表")
        // ⑤ラベルやグラフなどの機能、デザインなどを設定
        // x軸のラベルをbottomに表示
        mBinding.dayPagerLineChart.xAxis.position = XAxis.XAxisPosition.BOTTOM

        // ⑥グラフのデータに格納
        mBinding.dayPagerLineChart.data = LineData(lineDataSet)

        // 現在の日時
        val currentDateTime = LocalDateTime.now()

        /* グラフデザイン */
        // グラフの線の太さ
        lineDataSet.lineWidth = 3.0f
        // グラフモード(曲線)
        lineDataSet.mode = LineDataSet.Mode.CUBIC_BEZIER
        // グラフの色
        lineDataSet.colors = listOf(Color.BLUE)
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
        lineDataSet.setDrawFilled(false)
        // 値非表示
        lineDataSet.setDrawValues(false)
        // ポインタ非表示
        lineDataSet.setDrawCircles(false)
        // 値のテキストサイズ
        lineDataSet.valueTextSize = 10f
        // アイコンのオフセット
//        lineDataSet.iconsOffset = MPPointF(0f,2f)
        // 塗りつぶしの下限値を強制
//        lineDataSet.fillFormatter = IFillFormatter { _, _ ->
//            // 画面下端まで塗りつぶしたいので十分小さい値を指定
//            -200f   // 例: 潮位データの最小値-50程度
//        }
        // 必要ならY軸も合わせて調整
//        val minTide = hourlyTideLevelList.minOrNull() ?: 0f
//        val axisMin = if (minTide < 0) minTide - 10 else 0f
//        mBinding.dayPagerLineChart.axisLeft.axisMinimum = axisMin

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
        mBinding.dayPagerLineChart.moveViewToX((dataEntries.size - 1).toFloat()) // ※

        // レンダラー作成
        mCustomLineChartRenderer = CustomLineChartRenderer(
            mBinding.dayPagerLineChart,
            mBinding.dayPagerLineChart.animator,
            mBinding.dayPagerLineChart.viewPortHandler,
            tideFlowData.highTideTimes,
            tideFlowData.lowTideTimes,
            afterTideFlowData,
            mContext
        )

        // グラフアニメーションの描画時間
        val durationMillisX = 500
        val durationMillisY = 0
        val durationMillis = if(durationMillisX < durationMillisY){
            durationMillisY
        }else{
            durationMillisX
        }
        // グラフアニメーションの描画時間設定
        mBinding.dayPagerLineChart.animateXY(durationMillisX, durationMillisY)
        // 吹き出し時刻更新処理の呼び出し
        startRepeatingAtMinuteZero(isShowBubble = false, isShowDrawTideTimeLabels = false)
        /* 現日時の処理 */
        if(currentDateTime.dayOfMonth == tideFlowData.tideDate.third){
            // アニメーション終了後に以下を表示
            Handler(Looper.getMainLooper()).postDelayed({
                // 吹き出しとグリッドの表示
                mCustomLineChartRenderer!!.mIsShowBubble = true
                mCustomLineChartRenderer!!.mIsShowDrawTideTimeLabels = true
            }, durationMillis.toLong())
        }else{
            // アニメーション終了後に以下を表示
            Handler(Looper.getMainLooper()).postDelayed({
                // 満潮干潮時刻の表示
                mCustomLineChartRenderer!!.mIsShowDrawTideTimeLabels = true
            }, durationMillis.toLong())
        }

        // 表示されているデータの一番低い値を取得
        mBinding.dayPagerLineChart.lowestVisibleX
        // 表示されているデータの一番高い値を取得
        mBinding.dayPagerLineChart.highestVisibleX
        // ダブルタップでのズームを無効
        mBinding.dayPagerLineChart.isDoubleTapToZoomEnabled = false
        // グラフ拡大許可
        mBinding.dayPagerLineChart.setScaleEnabled(false)
        // タッチイベントを無効にする
        mBinding.dayPagerLineChart.setTouchEnabled(false)
//        // グラフ作成前に表示される文字列
//        mBinding.dayPagerLineChart.setNoDataText("")

        /* ラベルのカスタマイズ */
        // 右下のDescription Labelを表示
        mBinding.dayPagerLineChart.description.isEnabled = false
        // 右下のDescription Labelのテキスト
//        mBinding.dayPagerLineChart.description.text = mTideCondition
        // 右下のDescription Labelのテキストサイズ
//        mBinding.dayPagerLineChart.description.textSize = 30f
        // グラフ名ラベルを非表示
        mBinding.dayPagerLineChart.legend.isEnabled = false
        // Y軸右側ラベルを非表示
        mBinding.dayPagerLineChart.axisRight.isEnabled = false
        // 上からのオフセット
        mBinding.dayPagerLineChart.extraTopOffset = 30f

//        mBinding.dayPagerLineChart.setViewPortOffsets(0f, 0f, 0f, 0f)
//        mBinding.dayPagerLineChart.setExtraOffsets(0f, 0f, 0f, 0f)
//        mBinding.dayPagerLineChart.setPadding(0, 0, 0, 0)


        /* x軸関連 */
        mBinding.dayPagerLineChart.xAxis.apply {
            position = XAxis.XAxisPosition.BOTTOM
            setDrawGridLines(false)
            setDrawLabels(false)
            setDrawAxisLine(false)
            setAvoidFirstLastClipping(false)

            axisMinimum = if(beforeTideFlowData!=null){-1f}else{0f}
            axisMaximum = (dataEntries.size-2).toFloat()

            // 0,6,12,18,24 以外はラベル空白
            valueFormatter = object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    return value.toInt().toString()
                }

            }
            granularity = 2f
//            setLabelCount(5, false)
            labelCount = 6
            // ラベルのフォントサイズを大きくする（例：16fに）
            textSize = 12f
            // ラベルのカラーを白に（ダークテーマなら推奨）
            textColor = Color.LTGRAY
            // 太字を設定
            typeface = Typeface.DEFAULT_BOLD
            enableGridDashedLine(10f, 10f, 0f) // 点線
            gridColor = Color.DKGRAY            // 線の色
            gridLineWidth = 1f                  // 線の太さ
        }


        /* Y軸関連(左側) */
        mBinding.dayPagerLineChart.axisLeft.apply {
            // グリッド表示
            setDrawGridLines(false)
            // ラベル表示
            setDrawLabels(false)
            // 枠線表示
            setDrawAxisLine(false)
            // y軸ラベルの表示個数
            labelCount = 10
            // y左軸最大値
            axisMaximum = 200f
//            axisMaximum = 300f
            // y左軸最小値
            axisMinimum = -20f
//            axisMinimum = -100f
        }

        /* データのポインタを画像(Image)にする */
//        var icon: Drawable? = ResourcesCompat.getDrawable(getResources(), R.drawable.android, null)
//        val dataEntry = Entry(key.toFloat(), value.toFloat(), icon)
        // アイコンを表示する
        lineDataSet.setDrawIcons(true)
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

    }
}