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

    /* entriesリソース定義 */
    private val REGION_HOKKAIDO_TAB0 by lazy { resources.getStringArray(R.array.region_hokkaido_tab0)}
    private val REGION_TOHOKU_TAB0 by lazy { resources.getStringArray(R.array.region_tohoku_tab0)}
    private val REGION_KANTO_TAB0 by lazy { resources.getStringArray(R.array.region_kanto_tab0)}
    private val REGION_CHUBU_TAB0 by lazy { resources.getStringArray(R.array.region_chubu_tab0)}
    private val REGION_KINKI_TAB0 by lazy { resources.getStringArray(R.array.region_kinki_tab0)}
    private val REGION_CHUGOKU_TAB0 by lazy { resources.getStringArray(R.array.region_chugoku_tab0)}
    private val REGION_SHIKOKU_TAB0 by lazy { resources.getStringArray(R.array.region_shikoku_tab0)}
    private val REGION_KYUSHU_TAB0 by lazy { resources.getStringArray(R.array.region_kyushu_tab0)}
    private val REGION_NON by lazy { resources.getStringArray(R.array.region_non) }
    private val REGION_HOKKAIDO by lazy { resources.getStringArray(R.array.region_hokkaido)}
    private val REGION_TOHOKU by lazy { resources.getStringArray(R.array.region_tohoku)}
    private val REGION_KANTO by lazy { resources.getStringArray(R.array.region_kanto)}
    private val REGION_CHUBU by lazy { resources.getStringArray(R.array.region_chubu)}
    private val REGION_KINKI by lazy { resources.getStringArray(R.array.region_kinki)}
    private val REGION_CHUGOKU by lazy { resources.getStringArray(R.array.region_chugoku)}
    private val REGION_SHIKOKU by lazy { resources.getStringArray(R.array.region_shikoku)}
    private val REGION_KYUSHU by lazy { resources.getStringArray(R.array.region_kyushu)}


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
        // 観測地点を地方に紐づくロケーションに変更
        setSpinnerEntries(mBinding.location0Spinner, selectLocationTab0(SettingSharedPref(mContext).mRegion0SpinnerItem))
        setSpinnerEntries(mBinding.location1Spinner, selectLocation(SettingSharedPref(mContext).mRegion1SpinnerItem))
        setSpinnerEntries(mBinding.location2Spinner, selectLocation(SettingSharedPref(mContext).mRegion2SpinnerItem))
        setSpinnerEntries(mBinding.location3Spinner, selectLocation(SettingSharedPref(mContext).mRegion3SpinnerItem))
        setSpinnerEntries(mBinding.location4Spinner, selectLocation(SettingSharedPref(mContext).mRegion4SpinnerItem))
        // regionのポジション
        mBinding.region0Spinner.setSelection(SettingSharedPref(mContext).mRegion0SpinnerPosition)
        mBinding.region1Spinner.setSelection(SettingSharedPref(mContext).mRegion1SpinnerPosition)
        mBinding.region2Spinner.setSelection(SettingSharedPref(mContext).mRegion2SpinnerPosition)
        mBinding.region3Spinner.setSelection(SettingSharedPref(mContext).mRegion3SpinnerPosition)
        mBinding.region4Spinner.setSelection(SettingSharedPref(mContext).mRegion4SpinnerPosition)
        // locationのポジション
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

        /* 観測地点のスピナー選択後のイベント（region） */
        mBinding.region0Spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if(SettingSharedPref(mContext).mRegion0SpinnerPosition != position){
                    val spinner = parent as? Spinner
                    SettingSharedPref(mContext).mRegion0SpinnerPosition = position
                    SettingSharedPref(mContext).mRegion0SpinnerItem =
                        (spinner?.selectedItem as? String ?: "-").split(" ")[0]
                    // 観測地点を地方に紐づくロケーションに変更
                    setSpinnerEntries(mBinding.location0Spinner, selectLocationTab0(SettingSharedPref(mContext).mRegion0SpinnerItem))
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
        mBinding.region1Spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if(SettingSharedPref(mContext).mRegion1SpinnerPosition != position){
                    val spinner = parent as? Spinner
                    SettingSharedPref(mContext).mRegion1SpinnerPosition = position
                    SettingSharedPref(mContext).mRegion1SpinnerItem =
                        (spinner?.selectedItem as? String ?: "-").split(" ")[0]
                    println("SettingSharedPref(mContext).mRegion1SpinnerItem  ${SettingSharedPref(mContext).mRegion1SpinnerItem}")
                    // 観測地点を地方に紐づくロケーションに変更
                    setSpinnerEntries(mBinding.location1Spinner, selectLocation(SettingSharedPref(mContext).mRegion1SpinnerItem))
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
        mBinding.region2Spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if(SettingSharedPref(mContext).mRegion2SpinnerPosition != position){
                    val spinner = parent as? Spinner
                    SettingSharedPref(mContext).mRegion2SpinnerPosition = position
                    SettingSharedPref(mContext).mRegion2SpinnerItem =
                        (spinner?.selectedItem as? String ?: "-").split(" ")[0]
                    // 観測地点を地方に紐づくロケーションに変更
                    setSpinnerEntries(mBinding.location2Spinner, selectLocation(SettingSharedPref(mContext).mRegion2SpinnerItem))
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
        mBinding.region3Spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if(SettingSharedPref(mContext).mRegion3SpinnerPosition != position){
                    val spinner = parent as? Spinner
                    SettingSharedPref(mContext).mRegion3SpinnerPosition = position
                    SettingSharedPref(mContext).mRegion3SpinnerItem =
                        (spinner?.selectedItem as? String ?: "-").split(" ")[0]
                    // 観測地点を地方に紐づくロケーションに変更
                    setSpinnerEntries(mBinding.location3Spinner, selectLocation(SettingSharedPref(mContext).mRegion3SpinnerItem))
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        mBinding.region4Spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if(SettingSharedPref(mContext).mRegion4SpinnerPosition != position){
                    val spinner = parent as? Spinner
                    SettingSharedPref(mContext).mRegion4SpinnerPosition = position
                    SettingSharedPref(mContext).mRegion4SpinnerItem =
                        (spinner?.selectedItem as? String ?: "-").split(" ")[0]
                    // 観測地点を地方に紐づくロケーションに変更
                    setSpinnerEntries(mBinding.location4Spinner, selectLocation(SettingSharedPref(mContext).mRegion4SpinnerItem))
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }


        /* 観測地点のスピナー選択後のイベント（location） */
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

    private fun setSpinnerEntries(spinner: Spinner, array: Array<String>){
        // ArrayAdapter を生成してセット
        val adapter = ArrayAdapter(
            mContext,
            android.R.layout.simple_spinner_item,
            array
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
    }

    private fun selectLocationTab0(locationName: String): Array<String>{
        return when(locationName){
            "北海道地方" -> REGION_HOKKAIDO_TAB0
            "東北地方" -> REGION_TOHOKU_TAB0
            "関東地方" -> REGION_KANTO_TAB0
            "中部地方" -> REGION_CHUBU_TAB0
            "近畿地方" -> REGION_KINKI_TAB0
            "中国地方" -> REGION_CHUGOKU_TAB0
            "四国地方" -> REGION_SHIKOKU_TAB0
            "九州地方" -> REGION_KYUSHU_TAB0
            else -> REGION_KANTO_TAB0
        }
    }

    private fun selectLocation(locationName: String): Array<String>{
        return when(locationName){
            "-" -> REGION_NON
            "北海道地方" -> REGION_HOKKAIDO
            "東北地方" -> REGION_TOHOKU
            "関東地方" -> REGION_KANTO
            "中部地方" -> REGION_CHUBU
            "近畿地方" -> REGION_KINKI
            "中国地方" -> REGION_CHUGOKU
            "四国地方" -> REGION_SHIKOKU
            "九州地方" -> REGION_KYUSHU
            else -> REGION_KANTO
        }
    }
}