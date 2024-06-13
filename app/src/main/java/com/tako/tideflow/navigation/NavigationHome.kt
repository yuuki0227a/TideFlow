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
        // データ取得処理とコールバックのセット
        mTideFlowManager.getTideFlowDataTxt(dateNow.year, locationName, this)
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
            // データ受信後の処理

            // 潮汐データをレコードごとのマップにする。
            val tideFlowDataMap = mTideFlowManager.getTideFlowDataMap(data)
            // tideFlowDataMapから一つのデータを取り出して、「年」を関数に渡す。
            for(tideFlowData in tideFlowDataMap){
                // ファイル出力。※存在する場合は上書き。
                mTideFlowManager.saveToTideFileTxt(mContext, tideFlowData.value.tideDate.first, data)
                break
            }

            /*TODO. 以下テスト*/
            val today = LocalDate.now()
            println("★★★　${tideFlowDataMap[Triple(today.year,today.monthValue,today.dayOfMonth)]}　★★★")
            println("★★★　${tideFlowDataMap.size}　★★★")
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