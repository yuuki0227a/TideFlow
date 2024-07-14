package com.tako.tideflow

import android.content.Context
import android.content.SharedPreferences

class SettingSharedPref(context: Context){
    companion object{
        // ファイル名
        private const val FILE_NAME = "setting"
        // キー
        private const val KEY_THEMES_SPINNER_ITEM = "key_themes_Spinner_item"
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
        private const val KEY_LOCATION_NAME_TAB_SELECTED = "key_location_name_tab_selected"
        private const val KEY_APP_VERSION_NAME = "key_app_version_name"
        private const val KEY_APP_VERSION_CODE = "key_app_version_code"
        // デフォルト値
        private const val DEFAULT_THEMES_SPINNER_ITEM = 0
        private const val DEFAULT_LOCATION_SPINNER_ITEM_0 = "東京"
        private const val DEFAULT_LOCATION_SPINNER_ITEM_1 = "横浜"
        private const val DEFAULT_LOCATION_SPINNER_ITEM_2 = "名古屋"
        private const val DEFAULT_LOCATION_SPINNER_ITEM_3 = "大阪"
        private const val DEFAULT_LOCATION_SPINNER_ITEM_4 = "神戸"
        private const val DEFAULT_LOCATION_SPINNER_POSITION = 0
        private const val DEFAULT_LOCATION_NAME_TAB_SELECTED = 0
        private const val DEFAULT_APP_VERSION_NAME = "1.0.0"
        private const val DEFAULT_APP_VERSION_CODE = 1L
        const val THEMES_SPINNER_POSITION_SYSTEM = -1
        const val THEMES_SPINNER_POSITION_LIGHT = 1
        const val THEMES_SPINNER_POSITION_DARK = 2
    }

    private val mSharedPreferences: SharedPreferences = context.getSharedPreferences(FILE_NAME , Context.MODE_PRIVATE)

    // テーマカラーのアイテム
    var mThemesSpinnerItem: Int = mSharedPreferences.getInt(KEY_THEMES_SPINNER_ITEM, DEFAULT_THEMES_SPINNER_ITEM)!!
        set(value) {
            mSharedPreferences.edit()
                .putInt(KEY_THEMES_SPINNER_ITEM,value)
                .apply()
            field = value
        }
    // 観測地点のスピナー0のアイテム
    var mLocation0SpinnerItem: String = mSharedPreferences.getString(KEY_LOCATION_0_SPINNER_ITEM, DEFAULT_LOCATION_SPINNER_ITEM_0)!!
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
    var mLocation1SpinnerItem: String = mSharedPreferences.getString(KEY_LOCATION_1_SPINNER_ITEM, DEFAULT_LOCATION_SPINNER_ITEM_1)!!
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
    var mLocation2SpinnerItem: String = mSharedPreferences.getString(KEY_LOCATION_2_SPINNER_ITEM, DEFAULT_LOCATION_SPINNER_ITEM_2)!!
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
    var mLocation3SpinnerItem: String = mSharedPreferences.getString(KEY_LOCATION_3_SPINNER_ITEM, DEFAULT_LOCATION_SPINNER_ITEM_3)!!
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
    var mLocation4SpinnerItem: String = mSharedPreferences.getString(KEY_LOCATION_4_SPINNER_ITEM, DEFAULT_LOCATION_SPINNER_ITEM_4)!!
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

    // 観測地点タブの選択ポジション
    var mLocationNameTabSelected: Int = mSharedPreferences.getInt(KEY_LOCATION_NAME_TAB_SELECTED, DEFAULT_LOCATION_NAME_TAB_SELECTED)
        set(value) {
            mSharedPreferences.edit()
                .putInt(KEY_LOCATION_NAME_TAB_SELECTED,value)
                .apply()
            field = value
        }

    // アプリのバージョンコード
    var mAppVersionCode: Long = mSharedPreferences.getLong(KEY_APP_VERSION_CODE, DEFAULT_APP_VERSION_CODE)
        set(value) {
            mSharedPreferences.edit()
                .putLong(KEY_APP_VERSION_CODE,value)
                .apply()
            field = value
        }
    // アプリのバージョン名
    var mAppVersionName: String = mSharedPreferences.getString(KEY_APP_VERSION_NAME, DEFAULT_APP_VERSION_NAME)!!
        set(value) {
            mSharedPreferences.edit()
                .putString(KEY_APP_VERSION_NAME,value)
                .apply()
            field = value
        }
}

