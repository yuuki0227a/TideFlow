package com.tako.tideflow

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
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

class DateAdapter(private val context: Context, date:Date):RecyclerView.Adapter<DateAdapter.DateAdapterHolder>() {

    // リスナー格納変数
    private lateinit var listener: ((Int, DateAdapterHolder) -> Unit)

    private val inflater = LayoutInflater.from(context)
    private var dateManager: DateManager = DateManager(date)
    private var dateList: List<Date> = dateManager.getDays()
//    private var mStepsArray: ArrayList<Steps> = arrayListOf()
//    private val setting: Setting = Setting.getNewSetting(context)

    class DateAdapterHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val linearLayoutCalender: LinearLayout = view.findViewById(R.id.linearLayoutCalender)
        val dateText: TextView = view.findViewById<TextView>(R.id.dateText)
        val calenderDayBlock: TextView = view.findViewById<TextView>(R.id.calenderDayBlock)
//        val progressBar: ProgressBar = view.findViewById<ProgressBar>(R.id.calenderProgressBar)
        lateinit var date: LocalDate
    }

//    init {
//        mStepsArray = StepsDBHelper(context).findByAllOrderByDateAsc()
//    }

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

        val date: Date = dateList[position]

        val sdf = SimpleDateFormat("yyyy-MM-dd")
        val localDate = LocalDate.parse(sdf.format(date))
        holder.date = localDate
//        val steps: Steps? = StepsDBHelper(context).findByStepsDate(localDate.toString())

        //日付のみ表示させる
        val dateFormat = SimpleDateFormat("d", Locale.JAPAN)
        holder.dateText.text = dateFormat.format(date)

        if (dateManager.isToday(date)) {
            holder.view.setBackgroundResource(R.drawable.border_today)
//            holder.view.setBackgroundColor(Color.YELLOW) //当日の背景は黄色
        }

        //日曜日を赤、土曜日を青に
        val colorId: Int =
            when (dateManager.getDayOfWeek(date)) {
                1 -> {
                    Color.RED
                }
                7 -> {
                    Color.BLUE
                }
                else -> {
                    Color.BLACK
                }
            }
        holder.dateText.setTextColor(colorId)

        //当月以外は灰色
        if (!dateManager.isCurrentMonth(date)) {
            holder.dateText.setTextColor(Color.LTGRAY)
            holder.calenderDayBlock.setTextColor(Color.LTGRAY)
        }

        //タップしたときの処理を追加
        holder.view.setOnClickListener {
            listener.invoke(position, holder)
        }


        /** プログレスバー設定 */
//        if (steps == null) {
//            holder.calenderDayBlock.isVisible = false
//            holder.progressBar.isVisible = false
//        }else{
//            holder.progressBar.max = steps.stepGoal
////        holder.progressBar.max = setting.stepsGoal.toInt()
//            holder.calenderDayBlock.text = "${steps.getStepCount()}"
//            if (steps.stepGoal <= steps.getStepCount()) {
//                holder.progressBar.secondaryProgress = steps.stepGoal
//            } else {
//                holder.progressBar.progress = steps.getStepCount()
//            }
//        }

        /** 背景設定 */
//        holder.linearLayoutCalender.setBackgroundResource(R.drawable.border_calendar)

    }


    // リスナー
    fun setOnItemClickListener(listener: (Int, DateAdapterHolder) -> Unit) {
        this.listener = listener
    }

}