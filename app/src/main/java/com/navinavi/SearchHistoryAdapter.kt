package com.navinavi

import android.view.LayoutInflater
import android.view.OrientationEventListener
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.navinavi.room.SearchHistoryEntity

class SearchHistoryAdapter(private val searchHistoryList: MutableList<SearchHistoryEntity>): RecyclerView.Adapter<SearchHistoryAdapter.ViewHolder>() {
    private lateinit var listener: OnSearchHistoryCellClickListener

    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val from = view.findViewById<TextView>(R.id.from)
        val to = view.findViewById<TextView>(R.id.to)
        val searchTypeDateTime = view.findViewById<TextView>(R.id.searchTypeDateTime)
        val itemView = view.findViewById<ConstraintLayout>(R.id.itemView)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SearchHistoryAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.search_histry_row, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: SearchHistoryAdapter.ViewHolder, position: Int) {
        val searchHistoryList = searchHistoryList[position]
        holder.from.text = searchHistoryList.from
        holder.to.text = searchHistoryList.to
        holder.searchTypeDateTime.text = searchHistoryList.searchTypeDateTime

        holder.itemView.setOnClickListener {
            listener.onItemClick(searchHistoryList.url)
        }
    }

    override fun getItemCount(): Int {
        return searchHistoryList.size
    }

    interface OnSearchHistoryCellClickListener {
        fun onItemClick(url: String)
    }
    fun setOnSearchHistoryCellClickListener(listener: OnSearchHistoryCellClickListener) {
        this.listener = listener
    }
}