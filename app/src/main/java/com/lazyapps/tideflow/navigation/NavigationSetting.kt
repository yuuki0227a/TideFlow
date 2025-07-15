package com.lazyapps.tideflow.navigation

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.lazyapps.tideflow.MainActivity
import com.lazyapps.tideflow.R
import com.lazyapps.tideflow.SettingSharedPref
import com.lazyapps.tideflow.databinding.NavigationSettingBinding

class NavigationSetting : Fragment() {
    private val ACTION_BAR_TITLE = "設定"

    companion object {
        var isChangeSettingCalorieMETsRadioGroup = false
    }

    private lateinit var mBinding: NavigationSettingBinding
    private lateinit var mContext: Context
    // テーマカラー変更前
    private var mThemesSpinnerItemBefore = 0


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        mBinding = NavigationSettingBinding.inflate(inflater, container, false)
        mContext = mBinding.root.context
//        mThemesSpinnerItemBefore = SettingSharedPref(mContext).mThemesSpinnerItem
         return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Crashlytics テスト用クラッシュ
//        throw RuntimeException("Test Crash: わざとクラッシュさせています")

        /* アプリ情報 */
        // バージョン名
        mBinding.versionTextview.text = SettingSharedPref(mContext).mAppVersionName

        /* スピナーの初期値(position) */
//        mBinding.themesSpinner.setSelection(SettingSharedPref(mContext).mThemesSpinnerItem)
        mBinding.location0Spinner.setSelection(SettingSharedPref(mContext).mLocation0SpinnerPosition)
        mBinding.location1Spinner.setSelection(SettingSharedPref(mContext).mLocation1SpinnerPosition)
        mBinding.location2Spinner.setSelection(SettingSharedPref(mContext).mLocation2SpinnerPosition)
        mBinding.location3Spinner.setSelection(SettingSharedPref(mContext).mLocation3SpinnerPosition)
        mBinding.location4Spinner.setSelection(SettingSharedPref(mContext).mLocation4SpinnerPosition)

        /* テーマカラーのスピナー選択後のイベント */
//        mBinding.themesSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
//            override fun onItemSelected(
//                parent: AdapterView<*>?,
//                view: View?,
//                position: Int,
//                id: Long
//            ) {
//                if(SettingSharedPref(mContext).mThemesSpinnerItem != position){
//                    // position: 0 システム　1 ライト　2 ダーク
//                    SettingSharedPref(mContext).mThemesSpinnerItem = position
//                    // viewIdの保存
//                    SettingSharedPref(mContext).mBottomNavViewId = R.id.navi_setting
//                    (activity as MainActivity).restartActivity()
//                }
//            }
//            override fun onNothingSelected(parent: AdapterView<*>?) {}
//        }

        /* 観測地点のスピナー選択後のイベント */
        mBinding.location0Spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val spinner = parent as? Spinner
                SettingSharedPref(mContext).mLocation0SpinnerPosition = position
                SettingSharedPref(mContext).mLocation0SpinnerItem =
                    (spinner?.selectedItem as? String ?: "-").split(" ")[0]
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
        mBinding.location1Spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val spinner = parent as? Spinner
                SettingSharedPref(mContext).mLocation1SpinnerPosition = position
                SettingSharedPref(mContext).mLocation1SpinnerItem =
                    (spinner?.selectedItem as? String ?: "-").split(" ")[0]
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
        mBinding.location2Spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val spinner = parent as? Spinner
                SettingSharedPref(mContext).mLocation2SpinnerPosition = position
                SettingSharedPref(mContext).mLocation2SpinnerItem =
                    (spinner?.selectedItem as? String ?: "-").split(" ")[0]
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
        mBinding.location3Spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val spinner = parent as? Spinner
                SettingSharedPref(mContext).mLocation3SpinnerPosition = position
                SettingSharedPref(mContext).mLocation3SpinnerItem =
                    (spinner?.selectedItem as? String ?: "-").split(" ")[0]
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        mBinding.location4Spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val spinner = parent as? Spinner
                SettingSharedPref(mContext).mLocation4SpinnerPosition = position
                SettingSharedPref(mContext).mLocation4SpinnerItem =
                    (spinner?.selectedItem as? String ?: "-").split(" ")[0]
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

    }

    override fun onStart() {
        super.onStart()

    }

    override fun onDestroy() {
        super.onDestroy()
    }

}