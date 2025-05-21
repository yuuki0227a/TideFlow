package com.solapps.tideflow

import android.app.Application
import android.content.res.Configuration

class App: Application() {
    override fun onCreate() {
        super.onCreate()

    }

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
//        val intent = Intent(this, MainActivity::class.java)
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
//        startActivity(intent)
        // 再起動フラグ
//        SettingSharedPref(applicationContext).mIsReStart = true
        // アプリを終了する
//        android.os.Process.killProcess(android.os.Process.myPid())
//        exitProcess(1)
    }

}