package com.navinavi

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.navinavi.room.SearchHistoryDAO
import com.navinavi.room.SearchHistoryDatabase
import com.navinavi.room.SearchHistoryEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SearchHistoryFragment : Fragment() {
    private lateinit var db: SearchHistoryDatabase
    private lateinit var dao: SearchHistoryDAO
    private lateinit var mAdapter: SearchHistoryAdapter
    private lateinit var searchHistoryList: MutableList<SearchHistoryEntity>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.db = Room.databaseBuilder(
            requireContext(),
            SearchHistoryDatabase::class.java,
            "searchHistory.db"
        ).build()
        this.dao = this.db.searchHistoryDAO()


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        lifecycleScope.launch {
            settingSearchHistoryRecyclerView()
        }
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search_history, container, false)
    }


    private suspend fun settingSearchHistoryRecyclerView() {
        withContext(Dispatchers.Main) {
            dao.get20().observe(viewLifecycleOwner, Observer {
                searchHistoryList = it as MutableList<SearchHistoryEntity>
                Log.d("roomTest", searchHistoryList.toString())

                val recyclerView = view?.findViewById<RecyclerView>(R.id.recyclerView)
                recyclerView?.layoutManager = LinearLayoutManager(requireContext())
                mAdapter = SearchHistoryAdapter(searchHistoryList)
                recyclerView?.adapter = mAdapter

                settingSearchHistoryClickListener()
            })
        }
    }

    private fun settingSearchHistoryClickListener() {
        mAdapter.setOnSearchHistoryCellClickListener(
            object : SearchHistoryAdapter.OnSearchHistoryCellClickListener {
                override fun onItemClick(url: String) {
                    val action = SearchHistoryFragmentDirections.actionSearchHistoryFragmentToSearchResultFragment(url)
                    findNavController().navigate(action)
                }

            }
        )
    }
}