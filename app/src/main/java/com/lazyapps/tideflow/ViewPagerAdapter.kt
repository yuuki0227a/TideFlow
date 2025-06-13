package com.lazyapps.tideflow

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.lazyapps.tideflow.navigation.NavigationHome
import java.time.LocalDate

class ViewPagerAdapter(
    fa: NavigationHome,
    tideFlowDataMap: HashMap<Triple<Int, Int, Int>, TideFlowManager.TideFlowData>,
    tideDatePosition: HashMap<Triple<Int, Int, Int>, Int>,
    locationMap: HashMap<String, String>,
) : FragmentStateAdapter(fa) {

    // ページリスト first::ポジション  second:fragment
    private val mFragments: ArrayList<Pair<Int, DayPagerFragment>> = arrayListOf()
    init {
        // fragmentのポジションと一致させる。
        var position = 0
        for ((tideDate, tideFlowData) in tideFlowDataMap) {
            val centerDate = LocalDate.of(tideDate.first, tideDate.second, tideDate.third)
            val beforeDate = centerDate.minusDays(1L)
            val afterDate = centerDate.plusDays(1L)
            val beforeTideFlowData = tideFlowDataMap.getOrDefault(Triple(beforeDate.year, beforeDate.monthValue, beforeDate.dayOfMonth), null)
            val afterTideFlowData = tideFlowDataMap.getOrDefault(Triple(afterDate.year, afterDate.monthValue, afterDate.dayOfMonth), null)

            // ポジションとfragmentを紐づける。
            val fragment = DayPagerFragment.newInstance(tideFlowData, locationMap, beforeTideFlowData, afterTideFlowData)
            mFragments.add(Pair(position, fragment))
            // NavHomeでポジションを使用するために日付とポジションを紐づける。(日付はMapのキーと紐づいている)
            tideDatePosition[tideDate] = position
            position++
        }
    }

    // ページの枚数
    override fun getItemCount(): Int = mFragments.size

    // ページの遷移先
    override fun createFragment(position: Int): Fragment {

        return mFragments[position].second
    }
}