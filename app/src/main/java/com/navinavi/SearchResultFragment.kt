package com.navinavi

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.room.Room
import com.navinavi.room.SearchHistoryDAO
import com.navinavi.room.SearchHistoryDatabase
import com.navinavi.room.SearchHistoryEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SearchResultFragment : Fragment() {
    private val args: SearchResultFragmentArgs by navArgs()

    private lateinit var db: SearchHistoryDatabase
    private lateinit var dao: SearchHistoryDAO

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
            withContext(Dispatchers.Main) {
                dao.get20().observe(viewLifecycleOwner, Observer {
                    val list: MutableList<SearchHistoryEntity> = it as MutableList<SearchHistoryEntity>
                    Log.d("roomTest", list.toString())
                    for (item in list) {
                        Log.d("roomTest", item.id.toString())
                        Log.d("roomTest", item.from)
                        Log.d("roomTest", item.to)
                        Log.d("roomTest", item.searchTypeDateTime)
                        Log.d("roomTest", item.url)
                    }
                })

            }
        }

        return inflater.inflate(R.layout.fragment_search_result, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val searchResultWebView = view.findViewById<WebView>(R.id.webView)
        searchResultWebView.settings.javaScriptEnabled = true
        searchResultWebView.loadUrl(args.sendUrl)
        searchResultWebView.webViewClient = object : WebViewClient(){
            override fun shouldOverrideUrlLoading(
                view: WebView?,
                request: WebResourceRequest?
            ): Boolean {
                return super.shouldOverrideUrlLoading(view, request)
            }
        }
    }
}