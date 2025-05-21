package com.solapps.tideflow.navigation

import android.content.Context
import android.content.res.Configuration
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
import com.solapps.tideflow.CalendarFragment
import com.solapps.tideflow.LocationList
import com.solapps.tideflow.MainActivity
import com.solapps.tideflow.R
import com.solapps.tideflow.SettingSharedPref
import com.solapps.tideflow.TideFlowManager
import com.solapps.tideflow.Util
import com.solapps.tideflow.ViewPagerAdapter
import com.solapps.tideflow.databinding.NavigationHomeBinding
import java.io.File
import java.io.IOException
import java.lang.Exception
import java.time.LocalDate

class NavigationHome : Fragment(), TideFlowManager.DataFetchCallback {

    companion object{
        const val ERROR_COUNT_SEC = 5
        const val ERROR_COUNT_RESET = 0
        const val FILE_WRITE_FINISH_BEFORE = 0
        const val FILE_WRITE_FINISH_CURRENT = 1
        const val FILE_WRITE_FINISH_AFTER = 2
        var isFileWriteFinish: ArrayList<Boolean> = arrayListOf(false, false, false)
    }

    private lateinit var mBinding: NavigationHomeBinding
    private lateinit var mContext: Context
    private val mHandler: Handler by lazy { Handler(Looper.getMainLooper()) }
    private val mTideFlowManager: TideFlowManager by lazy { TideFlowManager() }
    // 観測地点の記号と名前を紐づけたMap
    private val mLocationMap: HashMap<String, String> by lazy { LocationList.getLocationNameMap() }
    // 潮汐データのロケーション毎のデータ群
    private val mTideFlowDataList: ArrayList<TideFlowData> = arrayListOf()
    // 選択中のタブポジション
    private var mTabSelectedPosition = 0

    data class TideFlowData(
        val viewPager2: ViewPager2,
        val linearLayout: LinearLayout,
        val tabLayoutPager: TabLayout,
        // 潮汐データをレコードごとのマップ
        var tideFlowDataMap: HashMap<Triple<Int, Int, Int>, TideFlowManager.TideFlowData> = hashMapOf(),
        // 潮汐データのレコードごとのリスト
        var tideFlowDataList: List<String> = listOf(),
        // 潮汐データの日付とページャーのポジション
        var tideDatePosition: HashMap<Triple<Int, Int, Int>, Int> = hashMapOf(),
        // 本日日付のポジション
        var todayPosition: Int = 0,
        // スクロール後のポジションを記憶
        var selectedPosition: Int = 0,
    )

    /** ナビゲーションメニューアイコン押下時など、画面作成時のみ通る
     * 更新処理では通らない */
//    override fun onCreate(savedInstanceState: Bundle?) {
//        println("onCreate")
//        println("onCreate  $savedInstanceState")
//        super.onCreate(savedInstanceState)
//        // フラグ初期化
//        isFileWriteFinish = arrayListOf(false, false, false)
//    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        // テーマが変更されたかどうかをチェック
        if ((newConfig.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES) {
            // ダークテーマが有効になった場合の処理
            onThemeChanged(true)
        } else {
            // ライトテーマが有効になった場合の処理
            onThemeChanged(false)
        }
    }

    private fun onThemeChanged(isDarkTheme: Boolean) {
        println("onThemeChanged $isDarkTheme")
        // テーマが変更された時の処理をここに書く
//        if (isDarkTheme) {
//            // ダークテーマになったときの処理
//        } else {
//            // ライトテーマになったときの処理
//        }
        (activity as MainActivity).restartActivity()
    }


    /**
     * onCreate()の後に通る
     * */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        println("onCreateView")
        super.onCreateView(inflater, container, savedInstanceState)
        mBinding = NavigationHomeBinding.inflate(inflater, container, false)
        mContext = mBinding.root.context

        // フラグ初期化
        isFileWriteFinish = arrayListOf(false, false, false)

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

        /*TODO. デバッグボタン*/
//        mBinding.lightButton.setOnClickListener {
//            (activity as MainActivity).changeToLightMode()
//        }
//        mBinding.darkButton.setOnClickListener {
//            (activity as MainActivity).changeToDarkMode()
//        }
//        mBinding.systemButton.setOnClickListener {
//            (activity as MainActivity).changeToSystemDefaultMode()
//        }


        return  mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        println("onViewCreated")
        super.onViewCreated(view, savedInstanceState)
        // ThreeTenBPの初期化
        AndroidThreeTen.init(mContext)
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
                if(!mBinding.homeLoadingProgressBarLinear.isVisible){
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
                    lordFragment(mTideFlowDataList[mTabSelectedPosition])
                }
                // ロード画面表示
                Util.showLoadingWindow(true, mBinding.homeLoadingProgressBarLinear, requireActivity())

            }
            override fun onTabReselected(tab: TabLayout.Tab?) {
                Log.i("tab","onTabReselected")
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {
                Log.i("tab","onTabUnselected")
            }
        })

        //TODO. 潮見表(大阪)ボタン
////        mBinding.sioMieyellButton.isVisible = false
//        mBinding.sioMieyellButton.setOnClickListener {
//            val url = "https://www.youtube.com/live/sbSKv5U0tAc?si=4kJFYH0EGy1_7agH"
////            val url = "https://koyomi8.com/moonage.html"
////            val url = "https://sio.mieyell.jp/select?po=52706"
//            openBrowser(mContext, url)
//        }
    }

    /**
     * 画面読み込み(viewpagerなど)
     * */
    private fun lordFragment(tideFlowData: TideFlowData){
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
                /*
                * ナビボタンを連打するとHOMEが更新される。
                * Destroy後に画面更新が通るので例外が出る。
                * */
                try {
                    // ロード画面非表示
                    Util.showLoadingWindow(false, mBinding.homeLoadingProgressBarLinear, requireActivity())
                }catch (e: IllegalStateException){
                    e.printStackTrace()
                }
            }
        }.start()
    }

    override fun onStart() {
        println("onStart")
        super.onStart()
        // ロード画面表示
        Util.showLoadingWindow(true, mBinding.homeLoadingProgressBarLinear, requireActivity())
        // タブポジションのデータ(観測地点)から画面を作成する。
        lordFragment(mTideFlowDataList[mTabSelectedPosition])


        /*TODO*/
//        mBinding.sioMieyellButton.setBackgroundColor(R.color.black)
//        mBinding.sioMieyellButton.setBackgroundColor(R.style.TextViewSpecificStyle)
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
    override fun onDataFetched(data: String, fileWriteFinishFlag: Int) {
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
            isFileWriteFinish[fileWriteFinishFlag] = true
        }
        println("ファイル出力 OK")
    }

    /**
     * 通信失敗時に呼ばれる関数
     * */
    private var mErrorCount = 0
    override fun onError(exception: IOException, fileWriteFinishFlag: Int, year: Int) {
        mHandler.post {
            mErrorCount++
            if(ERROR_COUNT_SEC <= mErrorCount){
                Toast.makeText(mContext, "通信失敗", Toast.LENGTH_SHORT).show()
            }
            isFileWriteFinish[fileWriteFinishFlag] = true
            val errorMsg = String.format(
                "%d年のデータ取得に失敗しました。", year
            )
            Toast.makeText(mContext, errorMsg, Toast.LENGTH_SHORT).show()
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

        // tabLayoutの初期ポジション(0～9)
        val initialPosition = SettingSharedPref(mContext).mLocationNameTabSelected
        val initialTab = mBinding.tabLayout.getTabAt(initialPosition)
        if (initialTab != null) {
//            mBinding.tabLayout.setScrollPosition(initialPosition, 0f, false)
            mBinding.tabLayout.selectTab(initialTab)
            // タブがスクロール状態の場合に、選択中のタブが真ん中に来るようにする。
            mBinding.tabLayout.post {
                val tabView = (mBinding.tabLayout.getChildAt(0) as ViewGroup).getChildAt(initialPosition)
                mBinding.tabLayout.scrollTo(tabView.left - (mBinding.tabLayout.width - tabView.width) / 2, 0)
            }
        }
    }

    /**
     * Fragment内のデータを更新する。
     * */
    private fun updateFragment(){
        println("updateFragment()")
        /* タブセンターの日付 */
        // カレンダーから遷移した場合は選択した日付を起点にする。
        val dateCenter = if (CalendarFragment.selectDate != null) {
            CalendarFragment.selectDate!!
        } else {
            LocalDate.now()
        }
        val dateMaxRange = dateCenter.plusDays(TideFlowManager.READ_DATA_RANGE.toLong())
        val dateMinRange = dateCenter.minusDays(TideFlowManager.READ_DATA_RANGE.toLong())

        // selectDateをnullに戻す。
        if(CalendarFragment.selectDate != null){
            CalendarFragment.selectDate = null
        }
        /* 年月日を分ける。 */
        val year = dateCenter.year
        val month = dateCenter.monthValue
        val day = dateCenter.dayOfMonth

        /* 選択中の観測地点の取得 */
        // タブレイアウトから選択中のロケーションのシンボルを取得
        val locationNameStr = Util.getKeyFromValue(
            mLocationMap,
            mBinding.tabLayout.getTabAt(mBinding.tabLayout.selectedTabPosition)?.text.toString()
        )
        // 観測地点
        val locationName = locationNameStr ?: ""
        if(locationName.isNotEmpty()){
            /* ファイル取得処理 */
            // ファイルの存在確認
            if (!File(mTideFlowManager.getFilePath(mContext, dateCenter.year, locationName)).exists()) {
                /* ない場合は取得する。ファイル出力も行う。 */
                // データ取得/出力処理とコールバックのセット(本年)
                mTideFlowManager.getTideFlowDataTxt(mContext, dateCenter.year, locationName, this, FILE_WRITE_FINISH_CURRENT)
            }else{
                isFileWriteFinish[FILE_WRITE_FINISH_CURRENT] = true
            }
            // 来年データを含む場合
            if(dateCenter.year < dateMaxRange.year){
                if (!File(mTideFlowManager.getFilePath(mContext, dateMaxRange.year, locationName)).exists()) {
                    // ※上記と同処理
                    mTideFlowManager.getTideFlowDataTxt(mContext, dateMaxRange.year, locationName, this, FILE_WRITE_FINISH_AFTER)
                }else{
                    isFileWriteFinish[FILE_WRITE_FINISH_AFTER] = true
                }
            }else{
                isFileWriteFinish[FILE_WRITE_FINISH_AFTER] = true
            }
            // 去年データを含む場合
            if(dateMinRange.year < dateCenter.year){
                if (!File(mTideFlowManager.getFilePath(mContext, dateMinRange.year, locationName)).exists()) {
                    // ※上記と同処理
                    mTideFlowManager.getTideFlowDataTxt(mContext, dateMinRange.year, locationName, this, FILE_WRITE_FINISH_BEFORE)
                }else{
                    isFileWriteFinish[FILE_WRITE_FINISH_BEFORE] = true
                }
            }else{
                isFileWriteFinish[FILE_WRITE_FINISH_BEFORE] = true
            }

            while(true){
                Thread.sleep(100L)
                if(isFileWriteFinish[0] && isFileWriteFinish[1] && isFileWriteFinish[2]){
                    break
                }
            }

            /* 潮汐データ作成処理 */
            // dateのリストをまとめる。
            val dateList: ArrayList<LocalDate> = arrayListOf(dateMinRange, dateCenter, dateMaxRange)
            // ファイル読み込み(Map)
            mTideFlowDataList[mTabSelectedPosition].tideFlowDataMap = mTideFlowManager.readFromTideFileTxt(mContext, dateList, locationName)

            // 潮汐データがない場合はリターンする。
            if(mTideFlowDataList[mTabSelectedPosition].tideFlowDataMap.isEmpty()){
                return
            }

            mHandler.post{
                try {
                    // ファイル読み込み(List)
                    mTideFlowDataList[mTabSelectedPosition].tideFlowDataList = mTideFlowManager.getTideFlowDataList(mContext, dateCenter.year, locationName)
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
                                tabTextView.setTextColor(ContextCompat.getColor(mContext, R.color.tab_date_center))
                            }
                        } else {
                            tabTextView.text = "-"
                        }

                        tab.customView = tabView
                    }.attach()
                }catch (e: Exception){
                    e.printStackTrace()
                }
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