package com.tako.tideflow.navigation

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.jakewharton.threetenabp.AndroidThreeTen
import com.tako.tideflow.LocationList
import com.tako.tideflow.R
import com.tako.tideflow.SettingSharedPref
import com.tako.tideflow.TideFlowManager
import com.tako.tideflow.Util
import com.tako.tideflow.Util.getMoonAge
import com.tako.tideflow.Util.openBrowser
import com.tako.tideflow.ViewPagerAdapter
import com.tako.tideflow.databinding.NavigationHomeBinding
import java.io.File
import java.io.IOException
import java.lang.Exception
import java.time.LocalDate

class NavigationHome : Fragment(), TideFlowManager.DataFetchCallback {

    companion object{
        const val ERROR_COUNT_SEC = 5
        const val ERROR_COUNT_RESET = 0
    }

    private lateinit var mBinding: NavigationHomeBinding
    private lateinit var mContext: Context
    private val mHandler: Handler by lazy { Handler(Looper.getMainLooper()) }
    private val mTideFlowManager: TideFlowManager by lazy { TideFlowManager() }
    // 観測地点の記号と名前を紐づけたMap
    private val mLocationMap: MutableMap<String, String> by lazy { LocationList.getLocationNameMap() }
    // 潮汐データのロケーション毎のデータ群
    private val mTideFlowDataList: ArrayList<TideFlowData> = arrayListOf()
    // 選択中のタブポジション
    private var mTabSelectedPosition = 0

    data class TideFlowData(
        val viewPager2: ViewPager2,
        val linearLayout: LinearLayout,
        val tabLayoutPager: TabLayout,
        // 潮汐データをレコードごとのマップ
        var tideFlowDataMap: MutableMap<Triple<Int, Int, Int>, TideFlowManager.TideFlowData> = mutableMapOf(),
        // 潮汐データのレコードごとのリスト
        var tideFlowDataList: List<String> = listOf(),
        // 潮汐データの日付とページャーのポジション
        var tideDatePosition: MutableMap<Triple<Int, Int, Int>, Int> = mutableMapOf(),
        // 本日日付のポジション
        var todayPosition: Int = 0,
        // スクロール後のポジションを記憶
        var selectedPosition: Int = 0,
    )

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

        /* メンバ初期化 */
        mTideFlowDataList.add(
            TideFlowData(
                mBinding.homeViewPager20, mBinding.linear0, mBinding.tabLayoutPager0
            )
        )
        mTideFlowDataList.add(
            TideFlowData(
                mBinding.homeViewPager21, mBinding.linear1, mBinding.tabLayoutPager1
            )
        )
        mTideFlowDataList.add(
            TideFlowData(
                mBinding.homeViewPager22, mBinding.linear2, mBinding.tabLayoutPager2
            )
        )
        mTideFlowDataList.add(
            TideFlowData(
                mBinding.homeViewPager23, mBinding.linear3, mBinding.tabLayoutPager3
            )
        )
        mTideFlowDataList.add(
            TideFlowData(
                mBinding.homeViewPager24, mBinding.linear4, mBinding.tabLayoutPager4
            )
        )
        mTideFlowDataList.add(
            TideFlowData(
                mBinding.homeViewPager25, mBinding.linear5, mBinding.tabLayoutPager5
            )
        )
        mTideFlowDataList.add(
            TideFlowData(
                mBinding.homeViewPager26, mBinding.linear6, mBinding.tabLayoutPager6
            )
        )
        mTideFlowDataList.add(
            TideFlowData(
                mBinding.homeViewPager27, mBinding.linear7, mBinding.tabLayoutPager7
            )
        )
        mTideFlowDataList.add(
            TideFlowData(
                mBinding.homeViewPager28, mBinding.linear8, mBinding.tabLayoutPager8
            )
        )
        mTideFlowDataList.add(
            TideFlowData(
                mBinding.homeViewPager29, mBinding.linear9, mBinding.tabLayoutPager9
            )
        )

        /* リスナー登録 */
        for(tideFlowData in mTideFlowDataList){
            registerListener(tideFlowData)
        }

        // タブポジションの読み込み
        mTabSelectedPosition = SettingSharedPref(mContext).mLocationNameTabSelected
        // 選択中の観測地点の画面を表示する。
        updatePositionView()

        // タブテキストの設定
        setLocationTabText()

        return  mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        println("onViewCreated")
        super.onViewCreated(view, savedInstanceState)
        // ThreeTenBPの初期化
        AndroidThreeTen.init(mContext)
        // タブポジションのデータ(観測地点)から画面を作成する。
        loadFragment(mTideFlowDataList[mTabSelectedPosition])
        // ロード画面表示
        showLoadingWindow(true)

    }

    /**
     * ロード画面表示と画面タッチの有効/無効を切り替える。
     * @param switch true:画面ON、タッチ無効　　false:画面OFF、タッチ有効
     * */
    private fun showLoadingWindow(switch: Boolean){
        when(switch){
            true -> {
                // ロード画面の表示
                mBinding.loadingProgressBarLinear.isVisible = true
                // タッチイベントを無効にする
                activity?.let { Util.disableUserInteraction(it) }
            }
            false -> {
                // ロード画面の表示
                mBinding.loadingProgressBarLinear.isVisible = false
                // タッチイベントを無効にする
                activity?.let { Util.enableUserInteraction(it) }
            }
        }
    }

    /**
     * viewのイベント登録
     * */
    private fun registerListener(tideFlowData: TideFlowData){
        // スクロールしたときのイベント
        tideFlowData.viewPager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                // ページが選択されたときの処理
//                Log.i("viewPager", "$position ページが選択")
                // スクロール選択後のポジションを記憶
                tideFlowData.selectedPosition = position
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

        // タブがタップされたときのイベント
        mBinding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                Log.i("tab","11111")
                if(!mBinding.loadingProgressBarLinear.isVisible){
                    Log.i("tab","11111-2")
                    mErrorCount = ERROR_COUNT_RESET
                    // 選択されたタブポジションを記録する。
                    if (tab != null) {
                        SettingSharedPref(mContext).mLocationNameTabSelected = tab.position
                    }else{
                        SettingSharedPref(mContext).mLocationNameTabSelected = 0
                    }
                    // ポジション更新
                    mTabSelectedPosition = SettingSharedPref(mContext).mLocationNameTabSelected
                    // 画面切り替え(観測地点の画面を表示する)
                    updatePositionView()
                    // タブポジションのデータ(観測地点)から画面を作成する。
                    loadFragment(mTideFlowDataList[mTabSelectedPosition])
                }
                // ロード画面表示
                showLoadingWindow(true)

            }
            override fun onTabReselected(tab: TabLayout.Tab?) {
                Log.i("tab","22222")
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {
                Log.i("tab","33333")
            }
        })

        //TODO. 潮見表(大阪)ボタン
        mBinding.sioMieyellButton.setOnClickListener {
            val url = "https://koyomi8.com/moonage.html"
//            val url = "https://sio.mieyell.jp/select?po=52706"
            openBrowser(mContext, url)
        }
    }

    /**
     * 画面読み込み(viewpagerなど)
     * */
    private fun loadFragment(tideFlowData: TideFlowData){
        Thread{
            while (tideFlowData.tideFlowDataMap.isEmpty()){
                // 画面更新
                updateFragment()
                Thread.sleep(1000)
                if(ERROR_COUNT_SEC <= mErrorCount){
                    break
                }
            }
            mHandler.post{
                // ロード画面非表示
                showLoadingWindow(false)
            }
        }.start()
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
     * ファイルがない場合はファイル出力を行う。
     * @param data 潮汐データの生データ。
     * */
    override fun onDataFetched(data: String) {
        // データの１行目だけ取り出す。(ファイル名に使用する項目を取り出すため)
        val tideFlowData = mTideFlowManager.getTideFlowData(data.lines()[0])
        mHandler.post {
            /* データ受信後の処理 */
            // ファイル出力。※存在する場合は上書き。
            mTideFlowManager.saveToTideFileTxt(
                mContext,
                tideFlowData.tideDate.first,
                data,
                tideFlowData.locationName
            )
//            Toast.makeText(mContext, "ダウンロード完了", Toast.LENGTH_SHORT).show()
        }
        println("ファイル出力 OK")
    }

    /**
     * 通信失敗時に呼ばれる関数
     * */
    private var mErrorCount = 0
    override fun onError(exception: IOException) {
        mHandler.post {
            mErrorCount++
            if(ERROR_COUNT_SEC <= mErrorCount){
                Toast.makeText(mContext, "通信失敗", Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     *　LocationNameTabのテキストの設定
     * */
    private fun setLocationTabText(){
        /* 観測地点 */
        // 各タブのインスタンスを取得してテキストを変更
        mBinding.tabLayout.getTabAt(0)?.text = SettingSharedPref(mContext).mLocation0SpinnerItem
        mBinding.tabLayout.getTabAt(1)?.text = SettingSharedPref(mContext).mLocation1SpinnerItem
        mBinding.tabLayout.getTabAt(2)?.text = SettingSharedPref(mContext).mLocation2SpinnerItem
        mBinding.tabLayout.getTabAt(3)?.text = SettingSharedPref(mContext).mLocation3SpinnerItem
        mBinding.tabLayout.getTabAt(4)?.text = SettingSharedPref(mContext).mLocation4SpinnerItem
        mBinding.tabLayout.getTabAt(5)?.text = SettingSharedPref(mContext).mLocation5SpinnerItem
        mBinding.tabLayout.getTabAt(6)?.text = SettingSharedPref(mContext).mLocation6SpinnerItem
        mBinding.tabLayout.getTabAt(7)?.text = SettingSharedPref(mContext).mLocation7SpinnerItem
        mBinding.tabLayout.getTabAt(8)?.text = SettingSharedPref(mContext).mLocation8SpinnerItem
        mBinding.tabLayout.getTabAt(9)?.text = SettingSharedPref(mContext).mLocation9SpinnerItem

        // tabLayoutの初期ポジション(0～9)
        val initialPosition = SettingSharedPref(mContext).mLocationNameTabSelected
        val initialTab = mBinding.tabLayout.getTabAt(initialPosition)
        if (initialTab != null) {
//            mBinding.tabLayout.setScrollPosition(initialPosition, 0f, false)
            mBinding.tabLayout.selectTab(initialTab)
            mBinding.tabLayout.post {
                val tabView = (mBinding.tabLayout.getChildAt(0) as ViewGroup).getChildAt(initialPosition)
                mBinding.tabLayout.scrollTo(tabView.left - (mBinding.tabLayout.width - tabView.width) / 2, 0)
            }
        }
    }

    /**
     * Fragment内のデータを更新する。
     * */
    var i = 0
    private fun updateFragment(){
        println("updateFragment():              $i")
        i++

        // 本日日付
        val dateNow = LocalDate.now()
        val year = dateNow.year
        val month = dateNow.monthValue
        val day = dateNow.dayOfMonth

        /* 選択中の観測地点の取得 */
        // タブレイアウトから選択中のロケーションのシンボルを取得
        val locationNameStr = Util.getKeyFromValue(
            mLocationMap,
            mBinding.tabLayout.getTabAt(mBinding.tabLayout.selectedTabPosition)?.text.toString()
        )
        // 観測地点
        val locationName = locationNameStr ?: ""
        if(locationName.isNotEmpty()){
            // ファイルの存在確認
            if (!File(mTideFlowManager.getFilePath(mContext, dateNow.year, locationName)).exists()) {
                /* ない場合は取得する。ファイル出力も行う。 */
                // データ取得/出力処理とコールバックのセット(本年)
                mTideFlowManager.getTideFlowDataTxt(mContext, dateNow.year, locationName, this)
            }
            // ファイル読み込み(Map)
            mTideFlowDataList[mTabSelectedPosition].tideFlowDataMap = mTideFlowManager.readFromTideFileTxt(mContext, dateNow, locationName)

            // 潮汐データがない場合はリターンする。
            if(mTideFlowDataList[mTabSelectedPosition].tideFlowDataMap.isEmpty()){
                return
            }

            mHandler.post{
                // ファイル読み込み(List)
                mTideFlowDataList[mTabSelectedPosition].tideFlowDataList = mTideFlowManager.getTideFlowDataList(mContext, dateNow.year, locationName)
                // ページャーアダプターのセット
                mTideFlowDataList[mTabSelectedPosition].viewPager2.adapter = ViewPagerAdapter(
                    this,
                    mTideFlowDataList[mTabSelectedPosition].tideFlowDataMap,
                    mTideFlowDataList[mTabSelectedPosition].tideDatePosition, mLocationMap)
                // 一応nullチェック
                if(mTideFlowDataList[mTabSelectedPosition].tideDatePosition[Triple(year, month, day)] != null){
                    // 本日日付のポジションを保持する。
                    mTideFlowDataList[mTabSelectedPosition].todayPosition = mTideFlowDataList[mTabSelectedPosition].tideDatePosition[Triple(year, month, day)]!!
                }
                // 本日日付に遷移させる。
                mTideFlowDataList[mTabSelectedPosition].viewPager2.setCurrentItem(
                    mTideFlowDataList[mTabSelectedPosition].todayPosition, false)

                /* タブビューとビューページャーの紐づけ */
                // TabLayoutMediatorを使用してページングをセットアップ
                TabLayoutMediator(mTideFlowDataList[mTabSelectedPosition].tabLayoutPager, mTideFlowDataList[mTabSelectedPosition].viewPager2) { tab, position ->
                    val date = Util.getKeyFromValue(mTideFlowDataList[mTabSelectedPosition].tideDatePosition, position)
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
                        }
                    } else {
                        tabTextView.text = "-"
                    }

                    tab.customView = tabView
                }.attach()
            }

        }
    }

    /**
     * タブで選択中の観測地点データ画面を表示する。(他は非表示)
     * */
    private fun updatePositionView(){
        /* タブポジションのデータ以外は非表示 */
        for(tideFlowData in mTideFlowDataList){
            tideFlowData.linearLayout.isVisible = false
        }
        // タブポジションのデータを表示する。
        mTideFlowDataList[mTabSelectedPosition].linearLayout.isVisible = true
    }



}