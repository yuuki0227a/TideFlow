package com.tako.tideflow.navigation

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
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

//    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding.textView8.text = "設定"
    }

    override fun onStart() {
        super.onStart()

    }

}