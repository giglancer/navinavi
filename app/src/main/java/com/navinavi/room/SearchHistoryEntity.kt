package com.navinavi.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class SearchHistoryEntity(
    @PrimaryKey(autoGenerate = true)
    var id: Int,
    val from: String,
    val to: String,
    val searchTypeDateTime: String,
    val url: String
    ) {

}
