package com.tako.tideflow

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.tako.tideflow.navigation.NavigationHome

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
            // ポジションとfragmentを紐づける。
            val fragment = DayPagerFragment.newInstance(tideFlowData, locationMap)
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