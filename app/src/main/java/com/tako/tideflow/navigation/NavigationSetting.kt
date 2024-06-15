package com.tako.tideflow.navigation

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.tako.tideflow.SettingSharedPref
import com.tako.tideflow.databinding.NavigationSettingBinding

class NavigationSetting : Fragment() {
    private val ACTION_BAR_TITLE = "設定"

    companion object {
        var isChangeSettingCalorieMETsRadioGroup = false
    }

    private lateinit var mBinding: NavigationSettingBinding
    private lateinit var mContext: Context


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        mBinding = NavigationSettingBinding.inflate(inflater, container, false)
        mContext = mBinding.root.context
         return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        /* スピナーの初期値(position) */
        mBinding.location0Spinner.setSelection(SettingSharedPref(mContext).mLocation0SpinnerPosition)
        mBinding.location1Spinner.setSelection(SettingSharedPref(mContext).mLocation1SpinnerPosition)
        mBinding.location2Spinner.setSelection(SettingSharedPref(mContext).mLocation2SpinnerPosition)
        mBinding.location3Spinner.setSelection(SettingSharedPref(mContext).mLocation3SpinnerPosition)
        mBinding.location4Spinner.setSelection(SettingSharedPref(mContext).mLocation4SpinnerPosition)
        mBinding.location5Spinner.setSelection(SettingSharedPref(mContext).mLocation5SpinnerPosition)
        mBinding.location6Spinner.setSelection(SettingSharedPref(mContext).mLocation6SpinnerPosition)
        mBinding.location7Spinner.setSelection(SettingSharedPref(mContext).mLocation7SpinnerPosition)
        mBinding.location8Spinner.setSelection(SettingSharedPref(mContext).mLocation8SpinnerPosition)
        mBinding.location9Spinner.setSelection(SettingSharedPref(mContext).mLocation9SpinnerPosition)

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
                    spinner?.selectedItem as? String ?: "-"
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
                    spinner?.selectedItem as? String ?: "-"
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
                    spinner?.selectedItem as? String ?: "-"
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
                    spinner?.selectedItem as? String ?: "-"
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
                    spinner?.selectedItem as? String ?: "-"
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
        mBinding.location5Spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val spinner = parent as? Spinner
                SettingSharedPref(mContext).mLocation5SpinnerPosition = position
                SettingSharedPref(mContext).mLocation5SpinnerItem =
                    spinner?.selectedItem as? String ?: "-"
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
        mBinding.location6Spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val spinner = parent as? Spinner
                SettingSharedPref(mContext).mLocation6SpinnerPosition = position
                SettingSharedPref(mContext).mLocation6SpinnerItem =
                    spinner?.selectedItem as? String ?: "-"
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
        mBinding.location7Spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val spinner = parent as? Spinner
                SettingSharedPref(mContext).mLocation7SpinnerPosition = position
                SettingSharedPref(mContext).mLocation7SpinnerItem =
                    spinner?.selectedItem as? String ?: "-"
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
        mBinding.location8Spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val spinner = parent as? Spinner
                SettingSharedPref(mContext).mLocation8SpinnerPosition = position
                SettingSharedPref(mContext).mLocation8SpinnerItem =
                    spinner?.selectedItem as? String ?: "-"
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
        mBinding.location9Spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val spinner = parent as? Spinner
                SettingSharedPref(mContext).mLocation9SpinnerPosition = position
                SettingSharedPref(mContext).mLocation9SpinnerItem =
                    spinner?.selectedItem as? String ?: "-"
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

    }

    override fun onStart() {
        super.onStart()

    }

}