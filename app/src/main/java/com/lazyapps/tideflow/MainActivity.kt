package com.lazyapps.tideflow

import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.lazyapps.tideflow.databinding.ActivityMainBinding
import java.util.Calendar

class MainActivity : AppCompatActivity() {
    private val mBinding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private lateinit var mNavController: NavController      // 下部芽メニュー管理
    private lateinit var mMenu: Menu                        // メニューのオブジェクト

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        if(SettingSharedPref(applicationContext).mIsReStart){
//            SettingSharedPref(applicationContext).mIsReStart = false
//            restartActivity()
//        }

        setContentView(mBinding.root)

        /* バージョン情報の取得と設定 */
        SettingSharedPref(mBinding.root.context).mAppVersionName = getVersionName()
        SettingSharedPref(mBinding.root.context).mAppVersionCode = getVersionCode()

        // システムのテーマ
//        when(SettingSharedPref(mBinding.root.context).mThemesSpinnerItem){
//            SettingSharedPref.THEMES_SPINNER_POSITION_SYSTEM -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
//            SettingSharedPref.THEMES_SPINNER_POSITION_LIGHT -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
//            SettingSharedPref.THEMES_SPINNER_POSITION_DARK -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
//            else -> {
//                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
//            }
//        }
        // ダークモード固定
//        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)

        // 下部メニュー
        initBottomNavigationView()

        //TODO. アラームのセット ※別アプリに移行予定
        //AlarmReceiver.setDailyAlarm(this)


    }

    // アクティビティの再起動メソッド
    fun restartActivity() {
        val intent = Intent(this, MainActivity::class.java)
        finish()
        startActivity(intent)
    }
//    fun changeToLightMode() {
//        SettingSharedPref(mBinding.root.context).mThemesSpinnerItem = SettingSharedPref.THEMES_SPINNER_POSITION_LIGHT
//        restartActivity()
//    }
//
//    fun changeToDarkMode() {
//        SettingSharedPref(mBinding.root.context).mThemesSpinnerItem = SettingSharedPref.THEMES_SPINNER_POSITION_DARK
//        restartActivity()
//    }
//
//    fun changeToSystemDefaultMode() {
//        SettingSharedPref(mBinding.root.context).mThemesSpinnerItem = SettingSharedPref.THEMES_SPINNER_POSITION_SYSTEM
//        restartActivity()
//    }

    override fun onStart() {
        super.onStart()
        /*TODO. テーマ対応*/
//        println("theme: ${Util.isDarkThemeOn(mBinding.root.context)}")
    }

    override fun onResume() {
        super.onResume()
        // バックグラウンドカラー
        val backgroundResId = getBackgroundResIdByTime()
        mBinding.root.setBackgroundResource(backgroundResId)
//        mBinding.linearlayoutBgColor.setBackgroundResource(backgroundResId)
    }

    /**
     * 下部メニューの初期化
     * */
    private fun initBottomNavigationView(){
        // 下部メニューコンポーネントの取得
        val bottomNavView: BottomNavigationView = findViewById(R.id.bottom_navigation)
        // ナビゲーションフラグメントの取得
        mNavController = findNavController(R.id.nav_host_fragment)
        // 下部メニューとナビゲーションを関連付け
        NavigationUI.setupWithNavController(bottomNavView, mNavController)

        /* 設定画面のテーマ変更後の再起動後処理 */
        // 再起動後の遷移先
        if(SettingSharedPref(mBinding.root.context).mBottomNavViewId != SettingSharedPref.DEFAULT_BOTTOM_NAV_VIEW_ID){
            bottomNavView.selectedItemId = SettingSharedPref(mBinding.root.context).mBottomNavViewId
            SettingSharedPref(mBinding.root.context).mBottomNavViewId = SettingSharedPref.DEFAULT_BOTTOM_NAV_VIEW_ID
        }
    }

    /**
     * メニュー関連
     * */
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        try {
            menuInflater.inflate(R.menu.top_menu, menu)
            this.mMenu = menu!! // 取得したmenuオブジェクトを保存する
            // MenuItemを取得する
            val startMenuItem = menu.findItem(R.id.action_start)
            val stopMenuItem = menu.findItem(R.id.action_stop)

        }catch (e: Exception) {
            Log.e("Exception", "${e.message}")
            for ((i, msg) in e.stackTrace.withIndex()) {
                Log.e(String.format("Exception %03d", i), "$msg")
            }
        }
        return true
    }

    // TODO. 現在未使用
    /**
     *
     * */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        try {

        }catch (e: Exception) {
            Log.e("Exception", "${e.message}")
            for ((i, msg) in e.stackTrace.withIndex()) {
                Log.e(String.format("Exception %03d", i), "$msg")
            }
        }
        return true
    }

    /**
     * バージョン情報の取得
     * */
    private fun getVersionName(): String {
        return try {
            val packageInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                packageManager.getPackageInfo(packageName, PackageManager.PackageInfoFlags.of(0))
            } else {
                @Suppress("DEPRECATION")
                packageManager.getPackageInfo(packageName, 0)
            }
            packageInfo.versionName ?: "N/A"
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
            "N/A"
        }
    }
    /**
     * バージョンコードの取得
     * */
    private fun getVersionCode(): Long {
        return try {
            val packageInfo: PackageInfo = packageManager.getPackageInfo(packageName, 0)
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
                packageInfo.longVersionCode
            } else {
                @Suppress("DEPRECATION")
                packageInfo.versionCode.toLong()
            }
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
            -1L
        }
    }

    /**
    * 時間帯別でバックグランドリソースを返す。
     * @return リソースファイルID
    * */
    private fun getBackgroundResIdByTime(): Int {
        val now = Calendar.getInstance()
        val hour = now.get(Calendar.HOUR_OF_DAY)
        val month = now.get(Calendar.MONTH) + 1

        val (sunrise, sunset) = when (month) {
            1 -> 7 to 17
            2 -> 6 to 17
            3 -> 6 to 18
            4 -> 5 to 18
            5 -> 5 to 18
            6 -> 5 to 19
            7 -> 5 to 19
            8 -> 5 to 18
            9 -> 5 to 18
            10 -> 6 to 17
            11 -> 6 to 17
            else -> 7 to 17
        }
        // TODO. Debug
//        return R.drawable.bg_debug
        return when (hour) {
            in (sunrise - 2) until sunrise -> R.drawable.bg_morning
            in sunrise until (sunrise + 4) -> R.drawable.bg_day
            in (sunrise + 4) until (sunset - 3) -> R.drawable.bg_noon
            in (sunset - 3) until sunset -> R.drawable.bg_evening
            else -> R.drawable.bg_night
        }
    }
}