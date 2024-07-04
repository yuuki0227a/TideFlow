package com.tako.tideflow

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.view.WindowManager
import android.widget.TextView
import java.lang.Exception

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


    private const val SPRING_TIDE = "大潮"
    private const val NEAD_TIDE = "中潮"
    private const val NEAP_TIDE = "小潮"
    private const val LONG_TIDE = "長潮"
    private const val YOUNG_TIDE = "若潮"
    /**
     * 月齢から潮の情報を返す。
     * @param lunarAge 月齢(少数)
     * @return 潮の状態を返す。範囲外の数値の場合、nullを返す。
     * */
    fun getTideInfoFromLunarPhase(lunarAge: Double): String? {
        return when(lunarAge.toInt()){
            29,0,1,2 -> SPRING_TIDE
            3,4,5,6 -> NEAD_TIDE
            7,8,9 -> NEAP_TIDE
            10 -> LONG_TIDE
            11 -> YOUNG_TIDE
            12,13 -> NEAD_TIDE
            14,15,16,17 -> SPRING_TIDE
            18,19,20,21 -> NEAD_TIDE
            22,23,24 -> NEAP_TIDE
            25 -> LONG_TIDE
            26 -> YOUNG_TIDE
            27,28 -> NEAD_TIDE
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

}