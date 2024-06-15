package com.tako.tideflow

import android.content.Context
import android.content.SharedPreferences

class SettingSharedPref(context: Context){
    companion object{
        // ファイル名
        private const val FILE_NAME = "setting"
        // キー
        private const val KEY_LOCATION_0_SPINNER_ITEM = "key_location_0_Spinner_item"
        private const val KEY_LOCATION_0_SPINNER_POSITION = "key_location_0_spinner_position"
        private const val KEY_LOCATION_1_SPINNER_ITEM = "key_location_1_Spinner_item"
        private const val KEY_LOCATION_1_SPINNER_POSITION = "key_location_1_spinner_position"
        private const val KEY_LOCATION_2_SPINNER_ITEM = "key_location_2_Spinner_item"
        private const val KEY_LOCATION_2_SPINNER_POSITION = "key_location_2_spinner_position"
        private const val KEY_LOCATION_3_SPINNER_ITEM = "key_location_3_Spinner_item"
        private const val KEY_LOCATION_3_SPINNER_POSITION = "key_location_3_spinner_position"
        private const val KEY_LOCATION_4_SPINNER_ITEM = "key_location_4_Spinner_item"
        private const val KEY_LOCATION_4_SPINNER_POSITION = "key_location_4_spinner_position"
        private const val KEY_LOCATION_5_SPINNER_ITEM = "key_location_5_Spinner_item"
        private const val KEY_LOCATION_5_SPINNER_POSITION = "key_location_5_spinner_position"
        private const val KEY_LOCATION_6_SPINNER_ITEM = "key_location_6_Spinner_item"
        private const val KEY_LOCATION_6_SPINNER_POSITION = "key_location_6_spinner_position"
        private const val KEY_LOCATION_7_SPINNER_ITEM = "key_location_7_Spinner_item"
        private const val KEY_LOCATION_7_SPINNER_POSITION = "key_location_7_spinner_position"
        private const val KEY_LOCATION_8_SPINNER_ITEM = "key_location_8_Spinner_item"
        private const val KEY_LOCATION_8_SPINNER_POSITION = "key_location_8_spinner_position"
        private const val KEY_LOCATION_9_SPINNER_ITEM = "key_location_9_Spinner_item"
        private const val KEY_LOCATION_9_SPINNER_POSITION = "key_location_9_spinner_position"

        private const val KEY_LOCATION_NAME_TAB_SELECTED = "key_location_name_tab_selected"
        // デフォルト値
        private const val DEFAULT_LOCATION_SPINNER_ITEM = "-"
        private const val DEFAULT_LOCATION_SPINNER_POSITION = 0
        private const val DEFAULT_LOCATION_NAME_TAB_SELECTED = 0
    }

    private val mSharedPreferences: SharedPreferences = context.getSharedPreferences(FILE_NAME , Context.MODE_PRIVATE)

    // 観測地点のスピナー0のアイテム
    var mLocation0SpinnerItem: String = mSharedPreferences.getString(KEY_LOCATION_0_SPINNER_ITEM, DEFAULT_LOCATION_SPINNER_ITEM)!!
        set(value) {
            mSharedPreferences.edit()
                .putString(KEY_LOCATION_0_SPINNER_ITEM,value)
                .apply()
            field = value
        }
    // 観測地点のスピナー0のポジション
    var mLocation0SpinnerPosition: Int = mSharedPreferences.getInt(KEY_LOCATION_0_SPINNER_POSITION, DEFAULT_LOCATION_SPINNER_POSITION)
        set(value) {
            mSharedPreferences.edit()
                .putInt(KEY_LOCATION_0_SPINNER_POSITION,value)
                .apply()
            field = value
        }
    // 観測地点のスピナー1のアイテム
    var mLocation1SpinnerItem: String = mSharedPreferences.getString(KEY_LOCATION_1_SPINNER_ITEM, DEFAULT_LOCATION_SPINNER_ITEM)!!
        set(value) {
            mSharedPreferences.edit()
                .putString(KEY_LOCATION_1_SPINNER_ITEM,value)
                .apply()
            field = value
        }
    // 観測地点のスピナー1のポジション
    var mLocation1SpinnerPosition: Int = mSharedPreferences.getInt(KEY_LOCATION_1_SPINNER_POSITION, DEFAULT_LOCATION_SPINNER_POSITION)
        set(value) {
            mSharedPreferences.edit()
                .putInt(KEY_LOCATION_1_SPINNER_POSITION,value)
                .apply()
            field = value
        }
    // 観測地点のスピナー2のアイテム
    var mLocation2SpinnerItem: String = mSharedPreferences.getString(KEY_LOCATION_2_SPINNER_ITEM, DEFAULT_LOCATION_SPINNER_ITEM)!!
        set(value) {
            mSharedPreferences.edit()
                .putString(KEY_LOCATION_2_SPINNER_ITEM,value)
                .apply()
            field = value
        }
    // 観測地点のスピナー2のポジション
    var mLocation2SpinnerPosition: Int = mSharedPreferences.getInt(KEY_LOCATION_2_SPINNER_POSITION, DEFAULT_LOCATION_SPINNER_POSITION)
        set(value) {
            mSharedPreferences.edit()
                .putInt(KEY_LOCATION_2_SPINNER_POSITION,value)
                .apply()
            field = value
        }

    // 観測地点のスピナー3のアイテム
    var mLocation3SpinnerItem: String = mSharedPreferences.getString(KEY_LOCATION_3_SPINNER_ITEM, DEFAULT_LOCATION_SPINNER_ITEM)!!
        set(value) {
            mSharedPreferences.edit()
                .putString(KEY_LOCATION_3_SPINNER_ITEM,value)
                .apply()
            field = value
        }
    // 観測地点のスピナー3のポジション
    var mLocation3SpinnerPosition: Int = mSharedPreferences.getInt(KEY_LOCATION_3_SPINNER_POSITION, DEFAULT_LOCATION_SPINNER_POSITION)
        set(value) {
            mSharedPreferences.edit()
                .putInt(KEY_LOCATION_3_SPINNER_POSITION,value)
                .apply()
            field = value
        }
    // 観測地点のスピナー4のアイテム
    var mLocation4SpinnerItem: String = mSharedPreferences.getString(KEY_LOCATION_4_SPINNER_ITEM, DEFAULT_LOCATION_SPINNER_ITEM)!!
        set(value) {
            mSharedPreferences.edit()
                .putString(KEY_LOCATION_4_SPINNER_ITEM,value)
                .apply()
            field = value
        }
    // 観測地点のスピナー4のポジション
    var mLocation4SpinnerPosition: Int = mSharedPreferences.getInt(KEY_LOCATION_4_SPINNER_POSITION, DEFAULT_LOCATION_SPINNER_POSITION)
        set(value) {
            mSharedPreferences.edit()
                .putInt(KEY_LOCATION_4_SPINNER_POSITION,value)
                .apply()
            field = value
        }
    // 観測地点のスピナー5のアイテム
    var mLocation5SpinnerItem: String = mSharedPreferences.getString(KEY_LOCATION_5_SPINNER_ITEM, DEFAULT_LOCATION_SPINNER_ITEM)!!
        set(value) {
            mSharedPreferences.edit()
                .putString(KEY_LOCATION_5_SPINNER_ITEM,value)
                .apply()
            field = value
        }
    // 観測地点のスピナー5のポジション
    var mLocation5SpinnerPosition: Int = mSharedPreferences.getInt(KEY_LOCATION_5_SPINNER_POSITION, DEFAULT_LOCATION_SPINNER_POSITION)
        set(value) {
            mSharedPreferences.edit()
                .putInt(KEY_LOCATION_5_SPINNER_POSITION,value)
                .apply()
            field = value
        }
    // 観測地点のスピナー6のアイテム
    var mLocation6SpinnerItem: String = mSharedPreferences.getString(KEY_LOCATION_6_SPINNER_ITEM, DEFAULT_LOCATION_SPINNER_ITEM)!!
        set(value) {
            mSharedPreferences.edit()
                .putString(KEY_LOCATION_6_SPINNER_ITEM,value)
                .apply()
            field = value
        }
    // 観測地点のスピナー6のポジション
    var mLocation6SpinnerPosition: Int = mSharedPreferences.getInt(KEY_LOCATION_6_SPINNER_POSITION, DEFAULT_LOCATION_SPINNER_POSITION)
        set(value) {
            mSharedPreferences.edit()
                .putInt(KEY_LOCATION_6_SPINNER_POSITION,value)
                .apply()
            field = value
        }
    // 観測地点のスピナー7のアイテム
    var mLocation7SpinnerItem: String = mSharedPreferences.getString(KEY_LOCATION_7_SPINNER_ITEM, DEFAULT_LOCATION_SPINNER_ITEM)!!
        set(value) {
            mSharedPreferences.edit()
                .putString(KEY_LOCATION_7_SPINNER_ITEM,value)
                .apply()
            field = value
        }
    // 観測地点のスピナー7のポジション
    var mLocation7SpinnerPosition: Int = mSharedPreferences.getInt(KEY_LOCATION_7_SPINNER_POSITION, DEFAULT_LOCATION_SPINNER_POSITION)
        set(value) {
            mSharedPreferences.edit()
                .putInt(KEY_LOCATION_7_SPINNER_POSITION,value)
                .apply()
            field = value
        }
    // 観測地点のスピナー8のアイテム
    var mLocation8SpinnerItem: String = mSharedPreferences.getString(KEY_LOCATION_8_SPINNER_ITEM, DEFAULT_LOCATION_SPINNER_ITEM)!!
        set(value) {
            mSharedPreferences.edit()
                .putString(KEY_LOCATION_8_SPINNER_ITEM,value)
                .apply()
            field = value
        }
    // 観測地点のスピナー8のポジション
    var mLocation8SpinnerPosition: Int = mSharedPreferences.getInt(KEY_LOCATION_8_SPINNER_POSITION, DEFAULT_LOCATION_SPINNER_POSITION)
        set(value) {
            mSharedPreferences.edit()
                .putInt(KEY_LOCATION_8_SPINNER_POSITION,value)
                .apply()
            field = value
        }
    // 観測地点のスピナー9のアイテム
    var mLocation9SpinnerItem: String = mSharedPreferences.getString(KEY_LOCATION_9_SPINNER_ITEM, DEFAULT_LOCATION_SPINNER_ITEM)!!
        set(value) {
            mSharedPreferences.edit()
                .putString(KEY_LOCATION_9_SPINNER_ITEM,value)
                .apply()
            field = value
        }
    // 観測地点のスピナ9のポジション
    var mLocation9SpinnerPosition: Int = mSharedPreferences.getInt(KEY_LOCATION_9_SPINNER_POSITION, DEFAULT_LOCATION_SPINNER_POSITION)
        set(value) {
            mSharedPreferences.edit()
                .putInt(KEY_LOCATION_9_SPINNER_POSITION,value)
                .apply()
            field = value
        }

    // 観測地点タブの選択ポジション
    var mLocationNameTabSelected: Int = mSharedPreferences.getInt(KEY_LOCATION_NAME_TAB_SELECTED, DEFAULT_LOCATION_NAME_TAB_SELECTED)
        set(value) {
            mSharedPreferences.edit()
                .putInt(KEY_LOCATION_NAME_TAB_SELECTED,value)
                .apply()
            field = value
        }
}

