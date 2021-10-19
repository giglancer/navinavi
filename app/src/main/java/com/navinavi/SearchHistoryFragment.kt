package com.navinavi

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.navinavi.room.SearchHistoryEntity

class SearchHistoryFragment : Fragment() {

    lateinit var mAdapter: SearchHistoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search_history, container, false)
    }

    override fun onResume() {
        super.onResume()

        val searchHistory = SearchHistoryEntity(id = 0, from = "from", to = "to", searchTypeDateTime = "searchTypeDateTime", url = "url")
        val list = arrayListOf(searchHistory)

        val recyclerView = view?.findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView?.layoutManager = LinearLayoutManager(requireContext())

        mAdapter = SearchHistoryAdapter(list)
        recyclerView?.adapter = mAdapter

    }
}