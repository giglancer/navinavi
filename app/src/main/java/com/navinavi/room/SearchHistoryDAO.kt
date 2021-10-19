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

    // 昇順にして上から2１番目を削除
    @Query("delete from searchHistoryEntity where id = (select id from searchHistoryEntity order by id ASC limit 1 offset 22)")
    fun deleteOver20()

}