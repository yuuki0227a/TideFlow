package com.tako.tideflow

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.tako.tideflow.databinding.ActivityMainBinding
import java.lang.Exception

class MainActivity : AppCompatActivity() {
    private val mBinding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private lateinit var mNavController: NavController      // 下部芽メニュー管理
    private lateinit var mMenu: Menu                        // メニューのオブジェクト

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(mBinding.root)
    }

    override fun onResume() {
        super.onResume()
        // 下部メニュー
        initBottomNavigationView()
    }

    /**
     * 下部メニューの初期化
     * */
    private fun initBottomNavigationView(){
        // 下部メニューコンポーネントの取得
        val bottomNavView: BottomNavigationView = findViewById(R.id.bottom_navigation)
        // ナビゲーションフラグメントの取得
        mNavController = findNavController(R.id.nav_host_fragment)
        // 下部メニューとナビゲーションを関連付け
        NavigationUI.setupWithNavController(bottomNavView, mNavController)
    }

    /**
     * メニュー関連
     * */
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        try {
            menuInflater.inflate(R.menu.top_menu, menu)
            this.mMenu = menu!! // 取得したmenuオブジェクトを保存する
            // MenuItemを取得する
            val startMenuItem = menu.findItem(R.id.action_start)
            val stopMenuItem = menu.findItem(R.id.action_stop)

        }catch (e: Exception) {
            Log.e("Exception", "${e.message}")
            for ((i, msg) in e.stackTrace.withIndex()) {
                Log.e(String.format("Exception %03d", i), "$msg")
            }
        }
        return true
    }

    // TODO. 現在未使用
    /**
     *
     * */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        try {

        }catch (e: Exception) {
            Log.e("Exception", "${e.message}")
            for ((i, msg) in e.stackTrace.withIndex()) {
                Log.e(String.format("Exception %03d", i), "$msg")
            }
        }
        return true
    }
}