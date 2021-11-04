package com.navinavi.data

import android.net.Uri

data class SearchInformation(
    val baseUrl: String = "https://api.ekispert.jp//v1/json/search/course/light?key=",
    var from: String = "",
    var to: String = "",
    var date: String = "",
    var time: String = "",
    var searchType: String = ""
)
