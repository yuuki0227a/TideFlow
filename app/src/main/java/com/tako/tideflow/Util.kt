package com.tako.tideflow

import android.app.Activity
import android.graphics.Color
import android.view.WindowManager
import android.widget.TextView

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
}