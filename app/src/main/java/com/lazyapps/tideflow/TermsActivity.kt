package com.lazyapps.tideflow

import android.content.Intent
import android.os.Bundle
import android.webkit.JavascriptInterface
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import com.lazyapps.tideflow.databinding.ActivityTermsBinding

class TermsActivity : AppCompatActivity() {
    private val mBinding by lazy { ActivityTermsBinding.inflate(layoutInflater) }
    private val mContext by lazy { mBinding.root.context }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(mBinding.root)

        // TODO. debug
        SettingSharedPref(mBinding.root.context).mTermsVersionAgreed = 0

        // 同意ボタンは初期状態で無効化
        mBinding.buttonAgree.isEnabled = false

        // 利用規約バージョン
        val currentTermsVersion = TermsContract.CURRENT_TERMS_VERSION
        if (SettingSharedPref(mBinding.root.context).mTermsVersionAgreed < currentTermsVersion) {
            // 規約画面を表示（まだ同意していない）
            // 「同意する」ボタンの処理
            mBinding.buttonAgree.setOnClickListener {
                SettingSharedPref(mBinding.root.context).mTermsVersionAgreed = currentTermsVersion
                startActivity(Intent(this, MainActivity::class.java))

                // 初回起動フラグ確認
                if(SettingSharedPref(mContext).mIsInitialInstallation){
                    SettingSharedPref(mContext).mIsInitialInstallation = false
                    // region0/location0の初期値
                    SettingSharedPref(mContext).mRegion0SpinnerItem = "関東地方"
                    SettingSharedPref(mContext).mRegion0SpinnerPosition = 2
                    SettingSharedPref(mContext).mLocation0SpinnerItem = "東京"
                    SettingSharedPref(mContext).mLocation0SpinnerPosition = 10
                }

                finish()
            }

            // 「同意しない」ボタンの処理（アプリ終了）
            mBinding.buttonDisagree.setOnClickListener {
                finishAffinity() // アプリ全体を終了
            }

            // WebView設定
            val webView = mBinding.webView
            webView.settings.javaScriptEnabled = true
            webView.webViewClient = WebViewClient()
            webView.addJavascriptInterface(ScrollInterface(), "AndroidScroll")
            webView.loadUrl("file:///android_res/raw/terms.html")
        }else{
            // 同意済
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

    }

    inner class ScrollInterface {
        @JavascriptInterface
        fun onScrolledToBottom() {
            runOnUiThread {
                mBinding.buttonAgree.isEnabled = true
            }
        }
    }

}
