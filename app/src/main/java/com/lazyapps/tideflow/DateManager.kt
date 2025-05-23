package com.lazyapps.tideflow

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.jakewharton.threetenabp.AndroidThreeTen
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.*


class DateManager(private val date:Date){

    companion object{
        const val DAYS_OF_WEEK:Int=7 //1週間の日数 不変
        const val WEEKS_OF_CALENDAR:Int=6 //カレンダーに記載する週の数 今回は常に6週
    }

    private var calendar:Calendar = Calendar.getInstance()

    init {
        calendar.time=date
    }

    //6週間分の日数
    val dayCount:Int
        get()= DAYS_OF_WEEK * WEEKS_OF_CALENDAR


    fun getDays():List<Date>{

        //calendarの日数を変えていくので後で戻せるように現在の状態を保持
        val startDate:Date=calendar.time

        //当月のカレンダーに表示される前月分の日数を計算
        calendar.set(Calendar.DATE,1)//カレンダーをその月の一日に設定
        val dayOfWeek:Int=calendar.get(Calendar.DAY_OF_WEEK)-1//mCalendar.get(Calendar.DAY_OF_WEEK)で対象の日の曜日が1～7の数字で返される
        calendar.add(Calendar.DATE,-dayOfWeek)//カレンダーを月初めの週の日曜日に設定

        //カレンダーを1日ずつ進めながらdaysに追加
        val days: MutableList<Date> = mutableListOf()
        for(i in 0..dayCount){
            days.add(calendar.time)
            calendar.add(Calendar.DATE,1)
        }
        //状態を復元 (カレンダーを今日に戻す)
        calendar.time=startDate
        return days
    }

    //曜日を取得
    fun getDayOfWeek(date:Date):Int{
        return Calendar.getInstance().apply { time=date }.get(Calendar.DAY_OF_WEEK)
    }

    //今日かどうか
    fun isToday(date: Date): Boolean{
        val format: SimpleDateFormat = SimpleDateFormat("yyyy.MM.dd",Locale.JAPAN)
        val today:String=format.format(Calendar.getInstance().time)
        return today==format.format(date)
    }

    //当月かどうか確認
    fun isCurrentMonth(date: Date): Boolean{
        val format: SimpleDateFormat = SimpleDateFormat("yyyy.MM",Locale.JAPAN)
        val currentMonth:String=format.format(calendar.time)//現在時間を年月を取得(日時の情報は落とす)
        return currentMonth == format.format(date)
    }
}

class DateAdapter(val context: Context, date:Date):RecyclerView.Adapter<DateAdapter.DateAdapterHolder>() {

    // リスナー格納変数
    private lateinit var listener: ((Int, DateAdapterHolder) -> Unit)

    private val inflater = LayoutInflater.from(context)
    private var dateManager: DateManager = DateManager(date)
    private var dateList: List<Date> = dateManager.getDays()
    private val holidays = HolidayManager.readFromFile(context)

    init {
        // ThreeTenBPの初期化
        AndroidThreeTen.init(context)
    }

    class DateAdapterHolder(val view: View) : RecyclerView.ViewHolder(view) {
//        val calenderDayFrameLayout: FrameLayout = view.findViewById(R.id.calender_day_frameLayout)
        // 日付
        val dateText: TextView = view.findViewById(R.id.dateText)
        // 月齢
        val calenderDayMoonAgeTextview: TextView = view.findViewById(R.id.calender_day_moon_age_textview)
        // 潮状態
        val calenderDayTideConditionTextview: TextView = view.findViewById(R.id.calender_day_tide_condition_textview)
        // 月画像
        val calenderDayImageView: ImageView = view.findViewById(R.id.calender_day_imageView)
        // 月名
        val calenderDayMoonNameTextView: TextView = view.findViewById(R.id.calender_day_moon_name_textView)
        // ポジションの日付
        lateinit var date: LocalDate
    }

    //recyclerViewで並べる数
    override fun getItemCount(): Int {
        return dateManager.dayCount
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DateAdapterHolder {
        val view = inflater.inflate(R.layout.calendar_cell, parent, false)

        //viewのサイズを指定
        val params: RecyclerView.LayoutParams = RecyclerView.LayoutParams(
            parent.width / DateManager.DAYS_OF_WEEK,
            parent.height / DateManager.WEEKS_OF_CALENDAR
        )
        view.layoutParams = params
        return DateAdapterHolder(view)
    }

    @SuppressLint("SimpleDateFormat")
    override fun onBindViewHolder(holder: DateAdapterHolder, position: Int) {

        /* 日付の初期化 */
        // 日付をポジションから取得
        val date: Date = dateList[position]
        val sdf = SimpleDateFormat("yyyy-MM-dd")
        val localDate = LocalDate.parse(sdf.format(date))
        holder.date = localDate
        /* 月画像、月齢、潮状態の取得 */
        // threeten.bpのLocalDateの取得
        val bpLocalDate = org.threeten.bp.LocalDate.of(localDate.year, localDate.monthValue, localDate.dayOfMonth)
        // 月齢の取得
        val moonAge = Util.getMoonAge(bpLocalDate)
        // 少数第一位以下を切り捨てる。
//        val roundedMoonAge = String.format("%.1f", moonAge).toDouble()
        // 月齢から潮情報を取得する。
        val tideCondition = Util.getTideInfoFromLunarPhase(moonAge)

        /* データの表示 */
        // 日付のみ表示させる
        val dateFormat = SimpleDateFormat("d", Locale.JAPAN)
        holder.dateText.text = dateFormat.format(date)
        //TODO. 月齢 現在非表示
//        holder.calenderDayMoonAgeTextview.text = String.format("月齢 %.1f", roundedMoonAge)
        holder.calenderDayMoonAgeTextview.isVisible = false
        // 潮状態
        holder.calenderDayTideConditionTextview.text = tideCondition
        // 月名
        when(moonAge.toInt()){
            0 -> holder.calenderDayMoonNameTextView.text = "新月"
            7 -> holder.calenderDayMoonNameTextView.text = "上弦"
            15 -> holder.calenderDayMoonNameTextView.text = "満月"
            22 -> holder.calenderDayMoonNameTextView.text = "下弦"
            else -> holder.calenderDayMoonNameTextView.isVisible = false
        }
        // 月画像
        when(moonAge.toInt()){
            0 -> holder.calenderDayImageView.setImageResource(R.drawable.moon_00)
            1 -> holder.calenderDayImageView.setImageResource(R.drawable.moon_01)
            2 -> holder.calenderDayImageView.setImageResource(R.drawable.moon_02)
            3 -> holder.calenderDayImageView.setImageResource(R.drawable.moon_03)
            4 -> holder.calenderDayImageView.setImageResource(R.drawable.moon_04)
            5 -> holder.calenderDayImageView.setImageResource(R.drawable.moon_05)
            6 -> holder.calenderDayImageView.setImageResource(R.drawable.moon_06)
            7 -> holder.calenderDayImageView.setImageResource(R.drawable.moon_07)
            8 -> holder.calenderDayImageView.setImageResource(R.drawable.moon_08)
            9 -> holder.calenderDayImageView.setImageResource(R.drawable.moon_09)
            10 -> holder.calenderDayImageView.setImageResource(R.drawable.moon_10)
            11 -> holder.calenderDayImageView.setImageResource(R.drawable.moon_11)
            12 -> holder.calenderDayImageView.setImageResource(R.drawable.moon_12)
            13 -> holder.calenderDayImageView.setImageResource(R.drawable.moon_13)
            14 -> holder.calenderDayImageView.setImageResource(R.drawable.moon_14)
            15 -> holder.calenderDayImageView.setImageResource(R.drawable.moon_15)
            16 -> holder.calenderDayImageView.setImageResource(R.drawable.moon_16)
            17 -> holder.calenderDayImageView.setImageResource(R.drawable.moon_17)
            18 -> holder.calenderDayImageView.setImageResource(R.drawable.moon_18)
            19 -> holder.calenderDayImageView.setImageResource(R.drawable.moon_19)
            20 -> holder.calenderDayImageView.setImageResource(R.drawable.moon_20)
            21 -> holder.calenderDayImageView.setImageResource(R.drawable.moon_21)
            22 -> holder.calenderDayImageView.setImageResource(R.drawable.moon_22)
            23 -> holder.calenderDayImageView.setImageResource(R.drawable.moon_23)
            24 -> holder.calenderDayImageView.setImageResource(R.drawable.moon_24)
            25 -> holder.calenderDayImageView.setImageResource(R.drawable.moon_25)
            26 -> holder.calenderDayImageView.setImageResource(R.drawable.moon_26)
            27 -> holder.calenderDayImageView.setImageResource(R.drawable.moon_27)
            28 -> holder.calenderDayImageView.setImageResource(R.drawable.moon_28)
            29 -> holder.calenderDayImageView.setImageResource(R.drawable.moon_29)
            else -> holder.calenderDayImageView.setImageResource(R.drawable.moon_00)
        }

        // 当日の場合の設定
        if (dateManager.isToday(date)) {
            holder.view.setBackgroundResource(R.drawable.border_today)
//            holder.view.setBackgroundColor(Color.YELLOW) //当日の背景は黄色
        }

        //日曜日を赤、土曜日を青に
        val colorId: Int =
            when (dateManager.getDayOfWeek(date)) {
                1 -> {
                    ContextCompat.getColor(context, R.color.calender_sunday_text_color)
//                    R.color.calender_sunday_text_color
                }
                7 -> {
                    ContextCompat.getColor(context, R.color.calender_saturday_text_color)
                }
                else -> {
                    // 祝日の場合は赤にする。
                    if(!holidays[holder.date.toString()].isNullOrEmpty()){
                        ContextCompat.getColor(context, R.color.calender_holiday_text_color)
                    }else{
                        -1
                    }
                }
            }
        // 日付の色の設定
        if(colorId != -1){
            holder.dateText.setTextColor(colorId)
        }
        // 月齢、潮状態の色の設定
//        holder.calenderDayMoonAgeTextview.setTextColor(Color.BLACK)
//        holder.calenderDayTideConditionTextview.setTextColor(Color.BLACK)
        /*TODO. 非表示*/
        holder.calenderDayMoonNameTextView.isVisible = false

        // 潮状態ごとに色分けする。
        if(tideCondition != null){
            Util.setColorForTideType(context, holder.calenderDayTideConditionTextview, tideCondition)
        }

        // 月名の色
        holder.calenderDayMoonNameTextView.setTextColor(ContextCompat.getColor(context, R.color.calender_moon_name_text_color))

        //当月以外の場合の処理
        if (!dateManager.isCurrentMonth(date)) {
            /* テキストカラーを灰色にする。 */
            holder.dateText.setTextColor(ContextCompat.getColor(context, R.color.calender_disable_color))
            holder.calenderDayMoonAgeTextview.setTextColor(ContextCompat.getColor(context, R.color.calender_disable_color))
            holder.calenderDayTideConditionTextview.setTextColor(ContextCompat.getColor(context, R.color.calender_disable_color))
            holder.calenderDayMoonNameTextView.setTextColor(ContextCompat.getColor(context, R.color.calender_disable_color))
            holder.calenderDayImageView.setColorFilter(ContextCompat.getColor(context, R.color.calender_disable_color))
        }

        //タップしたときの処理を追加
        holder.view.setOnClickListener {
            if (dateManager.isCurrentMonth(date)) {
                listener.invoke(position, holder)
            }
        }

    }


    // リスナー
    fun setOnItemClickListener(listener: (Int, DateAdapterHolder) -> Unit) {
        this.listener = listener
    }

}