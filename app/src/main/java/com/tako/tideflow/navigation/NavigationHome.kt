package com.tako.tideflow.navigation

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.tako.tideflow.TideFlowManager
import com.tako.tideflow.databinding.NavigationHomeBinding
import java.io.File
import java.io.IOException
import java.time.LocalDate

class NavigationHome : Fragment(), TideFlowManager.DataFetchCallback {

    private lateinit var mBinding: NavigationHomeBinding
    private lateinit var mContext: Context
    private val mHandler: Handler by lazy { Handler(Looper.getMainLooper()) }
    private val mTideFlowManager: TideFlowManager by lazy { TideFlowManager() }
    // 潮汐データをレコードごとのマップにする。
    private var mTideFlowDataMap: MutableMap<Triple<Int, Int, Int>, TideFlowManager.TideFlowData> = mutableMapOf()

    /** ナビゲーションメニューアイコン押下時など、画面作成時のみ通る
     * 更新処理では通らない */
    override fun onCreate(savedInstanceState: Bundle?) {
        println("onCreate")
        super.onCreate(savedInstanceState)

    }

    /**
     * onCreate()の後に通る
     * */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        println("onCreateView")
        super.onCreateView(inflater, container, savedInstanceState)
        mBinding = NavigationHomeBinding.inflate(inflater, container, false)
        mContext = mBinding.root.context
        return  mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        println("onViewCreated")
        super.onViewCreated(view, savedInstanceState)
        mBinding.textView6.text = "潮位表！！"

        // 本日日付
        val dateNow = LocalDate.now()
        /*TODO. 観測地点は設定ファイルから取得する*/
        // 観測地点
        val locationName = "OS"
        // ファイルの存在確認
        if (!File(mTideFlowManager.getFilePath(mContext, dateNow.year, locationName)).exists()) {
            /* ない場合は取得する。ファイル出力も行う。 */
            // データ取得/出力処理とコールバックのセット(本年)
            mTideFlowManager.getTideFlowDataTxt(dateNow.year, locationName, this)
        }
        // ファイル読み込み
        mTideFlowDataMap = mTideFlowManager.readFromTideFileTxt(mContext, dateNow.year, locationName)
        println("mTideFlowDataMap: $mTideFlowDataMap")

        /*TODO. テスト*/
        val tideFlowData = mTideFlowDataMap[Triple(dateNow.year, dateNow.monthValue, dateNow.dayOfMonth)]
        if(tideFlowData != null){
            mBinding.textView6.text = String.format(
                "観測地点: %s\n" +
                        "日付: %s/%s/%s\n" +
                        "満潮時間: %s:%s\t\t潮位: %s\n" +
                        "干潮時間: %s:%s\t\t潮位: %s\n",
                tideFlowData.locationName,
                tideFlowData.tideDate.first, tideFlowData.tideDate.second, tideFlowData.tideDate.third,
                tideFlowData.highTideTimes[0].first, tideFlowData.highTideTimes[0].second, tideFlowData.highTideTimes[0].third,
                tideFlowData.lowTideTimes[0].first, tideFlowData.lowTideTimes[0].second, tideFlowData.lowTideTimes[0].third,
            )
        }
    }

    override fun onStart() {
        println("onStart")
        super.onStart()
    }

    override fun onResume() {
        println("onResume")
        super.onResume()
    }

    override fun onPause() {
        println("onPause")
        super.onPause()
    }

    override fun onStop() {
        println("onStop")
        super.onStop()
    }

    override fun onDestroy() {
        println("onDestroy")
        super.onDestroy()
//        println("onDestroy")
    }

    /**
     * 通信成功時に呼ばれる関数
     * ファイルがない場合はファイルう出力を行う。
     * @param data 潮汐データの生データ。
     * */
    override fun onDataFetched(data: String) {
        mHandler.post {
            /* データ受信後の処理 */
            // tideFlowDataMapから一つのデータを取り出して、「年」を関数に渡す。
            for(tideFlowData in mTideFlowDataMap){
                // ファイル出力。※存在する場合は上書き。
                mTideFlowManager.saveToTideFileTxt(mContext, tideFlowData.value.tideDate.first, data, tideFlowData.value.locationName)
                // mTideFlowDataMapにデータがなければ読み込む
                if(mTideFlowDataMap.isEmpty()){
                    mTideFlowDataMap = mTideFlowManager.readFromTideFileTxt(mContext, tideFlowData.value.tideDate.first, tideFlowData.value.locationName)
                }
                break
            }

            /*TODO. 以下テスト*/
            val today = LocalDate.now()
            println("★★★　${mTideFlowDataMap[Triple(today.year,today.monthValue,today.dayOfMonth)]}　★★★")
            println("★★★　${mTideFlowDataMap.size}　★★★")
        }
    }

    /**
     * 通信失敗時に呼ばれる関数
     * */
    override fun onError(exception: IOException) {
        mHandler.post {
            /*TODO. エラー時の処理が未実装*/
            mBinding.textView6.text = "Error: ${exception.message}"
        }
    }



    /** viewPager2を管理するクラス */
//    private inner class PagerAdapter(
//        fa: NavigationSteps,
//        private val fragmentMap: MutableMap<Int, FragmentMain>
//    ) : FragmentStateAdapter(fa), View.OnTouchListener {
//        // ページ数を取得
//        override fun getItemCount(): Int = fragmentMap.size
//        // スワイプ位置によって表示するFragmentを変更
//        override fun createFragment(position: Int): Fragment {
//            val fragment: Fragment = fragmentMap[position] as Fragment
//
////            mCreateFragmentPosition = position
//
//            return fragment
//        }
//
//        override fun onBindViewHolder(
//            holder: FragmentViewHolder,
//            position: Int,
//            payloads: MutableList<Any>
//        ) {
//            super.onBindViewHolder(holder, position, payloads)
////            Toast.makeText(context, "onBindViewHolder $position", Toast.LENGTH_SHORT).show()
//        }
//
//        @SuppressLint("ClickableViewAccessibility")
//        override fun onTouch(p0: View?, p1: MotionEvent?): Boolean {
////            Toast.makeText(context, "PagerAdapter onTouch", Toast.LENGTH_SHORT).show()
//            return true
//        }
//    }



}