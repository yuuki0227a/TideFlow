package com.solapps.tideflow

import android.app.Activity
import android.app.UiModeManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible

object Util {
    // 曜日をカッコでくくる。
    const val DAY_OF_WEEK_FORMAT = "(%s)"
    /**
     * LocalDateのDayOfWeekで取得した曜日を日本語にする。
     * @param en LocalDateのDayOfWeekで取得した曜日(英語)
     * */
    fun dayOfWeekENtoJP(en: String): String{
        var jp = ""
        when(en){
            "MONDAY" -> jp = "月"
            "TUESDAY" -> jp = "火"
            "WEDNESDAY" -> jp = "水"
            "THURSDAY" -> jp = "木"
            "FRIDAY" -> jp = "金"
            "SATURDAY" -> jp = "土"
            "SUNDAY" -> jp = "日"
        }
        return jp
    }

    /**
     * 曜日によってテキストの色を変える。※日本語のみ対応！
     * @param dayOfWeek 日本語の曜日(月火水木金土日)
     * @param textView 色を変更したいview
     * */
    fun dayOfWeekTextColorJP(dayOfWeek: String, textView: TextView) {
        when (dayOfWeek) {
            "土" -> textView.setTextColor(Color.BLUE)
            "日" -> textView.setTextColor(Color.RED)
        }
    }

    /**
     * Mapのvalueからkeyを取り出す。
     * */
    fun getKeyFromValue(map: Map<Triple<Int, Int, Int>, Int>, value: Int): Triple<Int, Int, Int>? {
        for ((key, mapValue) in map) {
            if (mapValue == value) {
                return key
            }
        }
        return null
    }
    /**
     * Mapのvalueからkeyを取り出す。
     * */
    fun getKeyFromValue(map: Map<String, String>, value: String): String? {
        for ((key, mapValue) in map) {
            if (mapValue == value) {
                return key
            }
        }
        return null
    }

    /**
     * 画面タッチを無効にする。
     * */
    fun disableUserInteraction(activity: Activity) {
        activity.window?.setFlags(
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
        )
    }

    /**
     * 画面タッチを有効にする。
     * */
    fun enableUserInteraction(activity: Activity) {
        activity.window?.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
    }


    const val TIDE_TYPE_SPRING = "大潮"
    const val TIDE_TYPE_MIDDLE = "中潮"
    const val TIDE_TYPE_NEAP = "小潮"
    const val TIDE_TYPE_LONG = "長潮"
    const val TIDE_TYPE_YOUNG = "若潮"
    /**
     * 月齢から潮の情報を返す。
     * @param lunarAge 月齢(少数)
     * @return 潮の状態を返す。範囲外の数値の場合、nullを返す。
     * */
    fun getTideInfoFromLunarPhase(lunarAge: Double): String? {
        return when(lunarAge.toInt()){
            29,0,1,2 -> TIDE_TYPE_SPRING
            3,4,5,6 -> TIDE_TYPE_MIDDLE
            7,8,9 -> TIDE_TYPE_NEAP
            10 -> TIDE_TYPE_LONG
            11 -> TIDE_TYPE_YOUNG
            12,13 -> TIDE_TYPE_MIDDLE
            14,15,16,17 -> TIDE_TYPE_SPRING
            18,19,20,21 -> TIDE_TYPE_MIDDLE
            22,23,24 -> TIDE_TYPE_NEAP
            25 -> TIDE_TYPE_LONG
            26 -> TIDE_TYPE_YOUNG
            27,28 -> TIDE_TYPE_MIDDLE
            else -> null
        }
    }

    /**
     * 指定URLで外部ブラウザ(規定ブラウザ)を開く。
     * @param context コンテキスト
     * @param url 開くページのURL
     * */
    fun openBrowser(context: Context, url: String) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(url)
        context.startActivity(intent)
    }

    /**
     * 日付から月齢を求める。
     * @param date [threeten.bp]のLocalDate
     * @return 月齢(少数あり)
     * */
    fun getMoonAge(date: org.threeten.bp.LocalDate): Double {
        // 2000年1月6日の満月日を基準日とする
        val baseDate = org.threeten.bp.LocalDate.of(2000, 1, 6)
        val days = org.threeten.bp.temporal.ChronoUnit.DAYS.between(baseDate, date)
        val synodicMonth = 29.53058867 // 平均朔望月（約29.53日）

        return (days % synodicMonth) // 月齢
    }

    /**
     * 潮の状態ごとにテキストカラーを指定する。
     * @param context コンテキスト
     * @param textView 色を変えるview
     * @param tideType 潮情報(大潮、小潮など)
     * */
    fun setColorForTideType(context: Context, textView: TextView, tideType: String) {
        val color = when (tideType) {
            TIDE_TYPE_SPRING -> ContextCompat.getColor(context, R.color.tide_spring)
            TIDE_TYPE_MIDDLE -> ContextCompat.getColor(context, R.color.tide_middle)
            TIDE_TYPE_NEAP -> ContextCompat.getColor(context, R.color.tide_neap)
            TIDE_TYPE_LONG -> ContextCompat.getColor(context, R.color.tide_long)
            TIDE_TYPE_YOUNG -> ContextCompat.getColor(context, R.color.tide_young)
            else -> ContextCompat.getColor(context, R.color.tide_def)  // デフォルトの色
        }
        textView.setTextColor(color)
    }

    /**
     * ロード画面表示と画面タッチの有効/無効を切り替える。(LinearLayoutの表示非表示)
     * @param switch true:画面ON、タッチ無効　　false:画面OFF、タッチ有効
     * */
    fun showLoadingWindow(switch: Boolean, linearLayout: LinearLayout, activity: Activity){
        when(switch){
            true -> {
                // ロード画面の表示
                linearLayout.isVisible = true
                // タッチイベントを無効にする
                activity.let { disableUserInteraction(it) }
            }
            false -> {
                // ロード画面の表示
                linearLayout.isVisible = false
                // タッチイベントを無効にする
                activity.let { enableUserInteraction(it) }
            }
        }
    }

    /**
     * ロード画面表示と画面タッチの有効/無効を切り替える。(FrameLayoutの表示非表示)
     * @param switch true:画面ON、タッチ無効　　false:画面OFF、タッチ有効
     * */
    fun showLoadingWindow(switch: Boolean, frameLayout: FrameLayout, activity: Activity){
        when(switch){
            true -> {
                // ロード画面の表示
                frameLayout.isVisible = true
                // タッチイベントを無効にする
                activity.let { disableUserInteraction(it) }
            }
            false -> {
                // ロード画面の表示
                frameLayout.isVisible = false
                // タッチイベントを無効にする
                activity.let { enableUserInteraction(it) }
            }
        }
    }

    /**
     * 現在のシステムのテーマを返す。
     * */
    fun isDarkThemeOn(context: Context): Boolean {
        val uiModeManager = context.getSystemService(Context.UI_MODE_SERVICE) as UiModeManager
        return uiModeManager.nightMode == UiModeManager.MODE_NIGHT_YES
    }

}