package com.navinavi.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface SearchHistoryDAO {

    @Insert
    fun insert(searchHistoryEntity: SearchHistoryEntity)

    @Query("select * from searchHistoryEntity order by id ASC limit 20")
    fun get20(): LiveData<List<SearchHistoryEntity>>

    @Query("delete from searchHistoryEntity where 20 < id")
    fun deleteOver20()

}