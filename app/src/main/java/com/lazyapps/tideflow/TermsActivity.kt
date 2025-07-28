package com.lazyapps.tideflow

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.lazyapps.tideflow.databinding.ActivityTermsBinding

class TermsActivity : AppCompatActivity() {
    private val mBinding by lazy { ActivityTermsBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(mBinding.root)

        SettingSharedPref(mBinding.root.context).mKeyTermsAgreed = false

        if (SettingSharedPref(mBinding.root.context).mKeyTermsAgreed){
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        // 「同意する」ボタンの処理
        mBinding.buttonAgree.setOnClickListener {
            SettingSharedPref(mBinding.root.context).mKeyTermsAgreed = true
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        // 「同意しない」ボタンの処理（アプリ終了）
        mBinding.buttonDisagree.setOnClickListener {
            finishAffinity() // アプリ全体を終了
        }

        // 利用規約テキスト（簡易例）
        mBinding.termsText.text = getString(R.string.terms_text)
    }
}
