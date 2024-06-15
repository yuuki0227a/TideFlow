package com.tako.tideflow.navigation

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import com.tako.tideflow.LocationList
import com.tako.tideflow.R
import com.tako.tideflow.TideFlowManager
import com.tako.tideflow.Util
import com.tako.tideflow.ViewPagerAdapter
import com.tako.tideflow.databinding.NavigationHomeBinding
import java.io.File
import java.io.IOException
import java.time.LocalDate

class NavigationHome : Fragment(), TideFlowManager.DataFetchCallback {

    private lateinit var mBinding: NavigationHomeBinding
    private lateinit var mContext: Context
    private val mHandler: Handler by lazy { Handler(Looper.getMainLooper()) }
    private val mTideFlowManager: TideFlowManager by lazy { TideFlowManager() }
    // 潮汐データをレコードごとのマップ
    private var mTideFlowDataMap: MutableMap<Triple<Int, Int, Int>, TideFlowManager.TideFlowData> = mutableMapOf()
    // 潮汐データをレコードごとのリスト
    private var mTideFlowDataList: List<String> = listOf()
    // 潮汐データの日付とページャーのポジション
    private var mTideDatePosition: MutableMap<Triple<Int, Int, Int>, Int> = mutableMapOf()
    // 本日日付のポジション
    private var mTodayPosition = 0
    // スクロール後のポジションを記憶
    private var mSelectedPosition = 0
    // 観測地点の記号と名前を紐づけたMap
    private val mLocationMap: MutableMap<String, String> by lazy { LocationList.getLocationNameMap() }

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
//        mBinding.textView6.text = "潮位表！！"

        // 本日日付
        val dateNow = LocalDate.now()
        val year = dateNow.year
        val month = dateNow.monthValue
        val day = dateNow.dayOfMonth
        /*TODO. 観測地点は設定ファイルから取得する*/
        // 観測地点
        val locationName = "OS"
        // ファイルの存在確認
        if (!File(mTideFlowManager.getFilePath(mContext, dateNow.year, locationName)).exists()) {
            /* ない場合は取得する。ファイル出力も行う。 */
            // データ取得/出力処理とコールバックのセット(本年)
            mTideFlowManager.getTideFlowDataTxt(dateNow.year, locationName, this)
        }

        // ファイル読み込み(Map)
        mTideFlowDataMap = mTideFlowManager.readFromTideFileTxt(mContext, dateNow.year, locationName)
        // ファイル読み込み(List)
        mTideFlowDataList = mTideFlowManager.getTideFlowDataList(mContext, dateNow.year, locationName)
        // ページャーアダプターのセット
        mBinding.homeViewPager2.adapter = ViewPagerAdapter(this, mTideFlowDataMap, mTideDatePosition, mLocationMap)
        /*TODO. 観測地点は設定ファイルから読み込む*/
        /* 観測地点 */
        // 各タブのインスタンスを取得してテキストを変更
        mBinding.tabLayout.getTabAt(0)?.text = "新しいTAB１"
        mBinding.tabLayout.getTabAt(1)?.text = "新しいTAB２"
        mBinding.tabLayout.getTabAt(2)?.text = "新しいTAB３"
        // 一応nullチェック
        if(mTideDatePosition[Triple(year, month, day)] != null){
            // 本日日付のポジションを保持する。
            mTodayPosition = mTideDatePosition[Triple(year, month, day)]!!
        }
        // 本日日付に遷移させる。
        mBinding.homeViewPager2.setCurrentItem(mTodayPosition, false)
        // スクロールしたときのイベント
        mBinding.homeViewPager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                // ページが選択されたときの処理
//                Log.i("viewPager", "$position ページが選択")
                // スクロール選択後のポジションを記憶
                mSelectedPosition = position
            }

            override fun onPageScrollStateChanged(state: Int) {
                // ページのスクロール状態が変化したときの処理
//                Log.i("viewPager", "ページのスクロール状態が変化")
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                // ページがスクロールされたときの処理
//                Log.i("viewPager", "$position ページがスクロールされた")
            }
        })

        /* タブビューとビューページャーの紐づけ */
        // TabLayoutMediatorを使用してページングをセットアップ
//        TabLayoutMediator(mBinding.tabLayoutPager, mBinding.homeViewPager2) { tab, position ->
//            // ポジションからキー(日付)を取得
//            val date = getKeyFromValue(mTideDatePosition, position)
//            if (date != null) {
//                // 現日時を取得
//                val currentDate = LocalDate.now()
//                // タブに表示する日付を取得
//                val tabDate = LocalDate.of(date.first, date.second, date.third)
//                // 曜日の取得
//                val dayOfWeek = Util.dayOfWeekENtoJP(tabDate.dayOfWeek.toString())
//                // 日付と曜日をセットする
//                tab.text = String.format("%2d/%2d(%s)", date.second, date.third, dayOfWeek)
////                if (tabDate.isEqual(currentDate)) {
////                    // 今日の日付の場合、タブのテキスト色を変更
////                    tab.view.setBackgroundColor(ContextCompat.getColor(mContext, androidx.appcompat.R.color.material_blue_grey_800)) // タブの背景色を変更する場合
////                }
//            } else {
//                tab.text = "-"
//            }
//        }.attach()
        TabLayoutMediator(mBinding.tabLayoutPager, mBinding.homeViewPager2) { tab, position ->
            val date = getKeyFromValue(mTideDatePosition, position)
            val tabView = LayoutInflater.from(mContext).inflate(R.layout.custom_tab, null)
            val tabTextView = tabView.findViewById<TextView>(R.id.tabTextView)

            if (date != null) {
                val currentDate = LocalDate.now()
                val tabDate = LocalDate.of(date.first, date.second, date.third)

                val dayOfWeek = Util.dayOfWeekENtoJP(tabDate.dayOfWeek.toString())
                tabTextView.text = String.format("%2d/%2d(%s)", date.second, date.third, dayOfWeek)

                if (tabDate.isEqual(currentDate)) {
                    // 今日の日付の場合、テキストの色を変更
                    tabTextView.setTextColor(ContextCompat.getColor(mContext, R.color.purple))
                } else {
                    // 他の日付の場合、通常のテキストの色を設定
//                    tabTextView.setTextColor(ContextCompat.getColor(mContext, R.color.black))
                }
            } else {
                tabTextView.text = "-"
            }

            tab.customView = tabView
        }.attach()


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
            Toast.makeText(mContext, "通信成功", Toast.LENGTH_LONG).show()
        }
    }

    /**
     * 通信失敗時に呼ばれる関数
     * */
    override fun onError(exception: IOException) {
        mHandler.post {
            Toast.makeText(mContext, "通信失敗", Toast.LENGTH_LONG).show()
        }
    }

    /**
     * mTideDatePositionのvalueからkeyを取り出す。
     * */
    fun getKeyFromValue(map: Map<Triple<Int, Int, Int>, Int>, value: Int): Triple<Int, Int, Int>? {
        for ((key, mapValue) in map) {
            if (mapValue == value) {
                return key
            }
        }
        return null
    }

}