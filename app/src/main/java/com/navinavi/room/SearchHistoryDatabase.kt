package com.navinavi.room

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [SearchHistoryEntity::class], version = 1)
abstract class SearchHistoryDatabase: RoomDatabase() {
    abstract fun searchHistoryDAO(): SearchHistoryDAO
}