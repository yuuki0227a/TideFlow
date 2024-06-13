package com.tako.tideflow.navigation

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.tako.tideflow.CalendarFragment
import com.tako.tideflow.databinding.NavigationCalenderBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class NavigationCalender : Fragment() {

    private lateinit var mBinding: NavigationCalenderBinding
    private lateinit var mContext: Context

//    private val SPAN_COUNT = 7
    private val calendarManager = CalendarManager()
    private lateinit var mCalendarViewPager: ViewPager2
    private lateinit var mMonthText: TextView


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

        // アダプターセット
        mCalendarViewPager.adapter =
            object : androidx.viewpager2.adapter.FragmentStateAdapter(this) {
                // 最大ページ数
                override fun getItemCount(): Int = CalendarManager.MAX_COUNT
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
            CalendarManager.TODAY_COUNT,
            false
        )
        return mBinding.root
    }

    override fun onResume() {
        super.onResume()
    }

    /** CalendarManagerクラス ***********************************************************/
    //主にFragmentに渡すDateの管理
    class CalendarManager() {
        companion object {
            const val MAX_COUNT: Int = 400
            const val TODAY_COUNT: Int = 200
            const val DATE_PATTERN: String = "yyyy年MM月"
        }

        private val mCalendar: Calendar = Calendar.getInstance()

        fun positionToDateString(position: Int): String {
            val nowDate: Date = mCalendar.time
            mCalendar.add(Calendar.MONTH, position - TODAY_COUNT)
            val format = SimpleDateFormat(DATE_PATTERN, Locale.JAPAN)
            // monthの0を消す  2023月03 → 2023年 3月
            val dateString: String = format.format(mCalendar.time).replace("年0", "年 ")
            mCalendar.time = nowDate
            return (dateString)
        }
    }
}

