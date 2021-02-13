package com.lib.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.lib.database.entitity.RepeatedTextEntity

@Dao
interface RepeatedTextDao {
    @Query("SELECT * from RepeatedTextEntity ORDER BY dateTime ASC")
    fun getAllRecentTexts(): LiveData<MutableList<RepeatedTextEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertRepeatedText(vararg repeatedTextEntity: RepeatedTextEntity)

    @Query("DELETE from RepeatedTextEntity WHERE recentTextToRepeat = :recentTextToRepeat")
    fun deleteRepeatedText(recentTextToRepeat: String)
}