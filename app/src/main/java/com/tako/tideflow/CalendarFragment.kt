package com.tako.tideflow

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tako.tideflow.navigation.NavigationCalender
import com.tako.tideflow.databinding.CalendarFragmentBinding
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.*

private const val ARG_DATE = "date"

class CalendarFragment : Fragment() {

    companion object {
        // 選択した日付
        var selectDate: LocalDate? = null

        //Fragmentに引数を渡す
        @JvmStatic
        fun newInstance(dateString: String) =
            CalendarFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_DATE, dateString)
                }
            }
    }

    private lateinit var mBinding: CalendarFragmentBinding
    private lateinit var mContext: Context

    private var mDate: Date? = null
    private lateinit var mCalendarRecyclerView: RecyclerView


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = CalendarFragmentBinding.inflate(inflater, container, false)
        mContext = mBinding.root.context
        mCalendarRecyclerView = mBinding.calendarRecyclerView
        return mBinding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //argumentに入ったstringをDateに直す
        arguments?.let {
            val format = SimpleDateFormat(NavigationCalender.CalendarManager.DATE_PATTERN, Locale.JAPAN)
            val dateText = it.getString(ARG_DATE) ?: ""
            mDate = format.parse(dateText)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (mDate != null) {
            val adapter = DateAdapter(requireContext(), mDate!!)

            mCalendarRecyclerView.adapter = adapter

            //並べ方を横7列で並べるように指定
            val layoutManager = GridLayoutManager(
                requireContext(),
                DateManager.DAYS_OF_WEEK,
                LinearLayoutManager.VERTICAL,
                false
            )
            mCalendarRecyclerView.layoutManager = layoutManager

            //タップされたときの処理
            adapter.setOnItemClickListener { _: Int, holder: DateAdapter.DateAdapterHolder ->
                // 選択した日付
                selectDate = holder.date
                // NavController取得
                val navController = findNavController()
                // ホーム画面に遷移
                navController.navigate(R.id.action_navi_calendar_to_navi_home)
//                if(MainActivity.positionDateMap[holder.date] == null){
//                    return@setOnItemClickListener
//                }
//                else{
//                    MainActivity.positionNow = MainActivity.positionDateMap[holder.date]
//                    NavigationSteps.mViewPager2.setCurrentItem(MainActivity.positionNow!!, false)
//                    // NavController取得
//                    val navController = findNavController()
//                    // 歩数画面(ホーム画面)に遷移
//                    navController.navigate(R.id.action_navi_calendar_to_navi_steps)
//                }
            }
        }

    }




}