package com.lib.database.entitity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class RepeatedTextEntity(
    @PrimaryKey var recentTextToRepeat: String,
    @ColumnInfo var dateTime: Long = System.currentTimeMillis()
)