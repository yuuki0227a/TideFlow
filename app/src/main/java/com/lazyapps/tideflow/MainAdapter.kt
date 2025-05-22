package com.lazyapps.tideflow

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.lazyapps.tideflow.databinding.FragmentBlankBinding

class MainAdapter(
    private val tideFlowDataList: List<String>,
    private val tideFlowManager: TideFlowManager
) :RecyclerView.Adapter<MainAdapter.MainViewHolder>() {
    override fun getItemCount(): Int = tideFlowDataList.size


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainViewHolder {
        val binding = FragmentBlankBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MainViewHolder(binding)
    }

    // データをViewHolderにバインドするメソッド
    // positionからバインドするビューの番号が受け取れる
    // その番号のデータをリストから取得しholderにセット
    override fun onBindViewHolder(holder: MainViewHolder, position: Int) {
        val tideFlowData = tideFlowManager.getTideFlowData(tideFlowDataList[position])

        holder.name.text = tideFlowData.locationName
    }

    // itemView：アイテムレイアウトファイルに対応するViewオブジェクト
    // 「fragment_item_list」の要素を取得しHolderを構築
    class MainViewHolder(private val binding: FragmentBlankBinding) : RecyclerView.ViewHolder(binding.root) {
        val name: TextView = binding.test
    }
}