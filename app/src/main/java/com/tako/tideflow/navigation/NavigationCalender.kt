package com.tako.tideflow.navigation

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.tako.tideflow.CalendarFragment
import com.tako.tideflow.HolidayManager
import com.tako.tideflow.Util
import com.tako.tideflow.databinding.NavigationCalenderBinding
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import java.util.Calendar
import java.util.Date
import java.util.Locale

class NavigationCalender : Fragment() {

    companion object{
        private const val HOLIDAYS_JP_GITHUB_IO_URL = "https://holidays-jp.github.io/api/v1/date.json"
    }

    private lateinit var mBinding: NavigationCalenderBinding
    private lateinit var mContext: Context

//    private val SPAN_COUNT = 7
    private val calendarManager = CalendarManager()
    private lateinit var mCalendarViewPager: ViewPager2
    private lateinit var mMonthText: TextView
    private val mHandler: Handler by lazy { Handler(Looper.getMainLooper()) }
    private var mHolidays: MutableMap<String, String> = mutableMapOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)

        // 初期化
        mBinding = NavigationCalenderBinding.inflate(inflater, container, false)
        mContext = mBinding.root.context

        // オブジェクトの取得
        mCalendarViewPager = mBinding.calendarViewPager
        mMonthText = mBinding.monthText

        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onStart() {
        super.onStart()
        // ロード画面表示
        Util.showLoadingWindow(true, mBinding.calendarLoadingProgressBarLinear, requireActivity())
        // Fragment内のデータを更新する。
        updateFragment()
    }

    override fun onResume() {
        super.onResume()
    }

    /**
     * Fragment内のデータを更新する。
     * */
    private fun updateFragment(){
        Thread {
            // 祝日ファイルがなければ取得/出力処理を行う。
            if(!HolidayManager.isExistsHolidayFile(mContext)){
                // 祝日の取得
                val response = fetchHolidayData(HOLIDAYS_JP_GITHUB_IO_URL)
                if(response.isNotEmpty()){
                    // 祝日をMapにする。
                    mHolidays = HolidayManager.parseHolidayData(response)
                    var isComp = false
                    Thread {
                        /* ファイル出力 */
                        if(mHolidays.isNotEmpty()){
                            HolidayManager.writeToFile(mContext, mHolidays)
                        }
                        isComp = true
                    }.start()
                    while(!isComp){
                        Thread.sleep(100L)
                    }
                }
            }

            mHandler.post {
                try {
                    // アダプターセット
                    mCalendarViewPager.adapter =
                        object : androidx.viewpager2.adapter.FragmentStateAdapter(this) {
                            // 最大ページ数
                            override fun getItemCount(): Int = CalendarManager.maxCount
                            // 画面作成
                            override fun createFragment(position: Int): Fragment {
                                return CalendarFragment.newInstance(
                                    calendarManager.positionToDateString(
                                        position
                                    )
                                )
                            }
                        }

                    // ViewPagerのイベント登録
                    mCalendarViewPager.registerOnPageChangeCallback(object :
                        ViewPager2.OnPageChangeCallback() {
                        //ViewPagerが別のスライドに変わったときに呼ばれる
                        override fun onPageSelected(position: Int) {
                            super.onPageSelected(position)
                            mMonthText.text = calendarManager.positionToDateString(position)
                        }
                    })

                    // スライドの向き
                    mCalendarViewPager.orientation = ViewPager2.ORIENTATION_HORIZONTAL //横方向にスライド
                    // 表示する画面ポジション
                    mCalendarViewPager.setCurrentItem(
                        CalendarManager.todayCount,
                        false
                    )
                    // ロード画面非表示
                    Util.showLoadingWindow(false, mBinding.calendarLoadingProgressBarLinear, requireActivity())
                }catch (e: Exception){
                    e.printStackTrace()
                }
            }
        }.start()
    }

    /**
     * 祝日データをURLから取得
     * @param urlString URL(文字列)
     * */
    private fun fetchHolidayData(urlString: String): String {
        var jsonBody = ""
        val url = URL(urlString)
        val connection = url.openConnection() as HttpURLConnection
        connection.requestMethod = "GET"
        try {
            jsonBody = connection.inputStream.bufferedReader().readText()
        }catch (e: Exception){
            e.printStackTrace()
        }
        return jsonBody
    }

    /** CalendarManagerクラス ***********************************************************/
    //主にFragmentに渡すDateの管理
    class CalendarManager() {
        companion object {
            const val MAX_COUNT: Int = 36
            var maxCount = MAX_COUNT
            const val TODAY_COUNT: Int = 18
            var todayCount = TODAY_COUNT
            const val DATE_PATTERN: String = "yyyy年MM月"
        }

        init {
            val dateNow = LocalDate.now()
            val dateLastYear = LocalDate.of(dateNow.minusYears(1L).year, 1 ,1)
            val dateNextYear = LocalDate.of(dateNow.plusYears(1L).year, 12 ,31)
            val monthsBefore = ChronoUnit.MONTHS.between(dateLastYear, dateNow).toInt()
            val monthsAfter = ChronoUnit.MONTHS.between(dateNow, dateNextYear).toInt() + 1
            todayCount = monthsBefore
            maxCount = monthsBefore + monthsAfter
        }

        private val mCalendar: Calendar = Calendar.getInstance()

        fun positionToDateString(position: Int): String {
            println("position  $position")
            val nowDate: Date = mCalendar.time
            mCalendar.add(Calendar.MONTH, position - todayCount)
            val format = SimpleDateFormat(DATE_PATTERN, Locale.JAPAN)
            // monthの0を消す  2023月03 → 2023年 3月
            val dateString: String = format.format(mCalendar.time).replace("年0", "年 ")
            mCalendar.time = nowDate
            return (dateString)
        }
    }
}

