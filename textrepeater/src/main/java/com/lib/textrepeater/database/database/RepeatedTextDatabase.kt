package com.lib.textrepeater.database.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.lib.textrepeater.database.dao.RepeatedTextDao
import com.lib.textrepeater.database.entitity.RepeatedTextEntity

@Database(entities = [RepeatedTextEntity::class], version = 1)
abstract class RepeatedTextDatabase: RoomDatabase() {
    abstract fun repeatedTextDao(): RepeatedTextDao

    companion object {
        private val REPEATED_TEXT_APP_DATABASE_NAME = "repeated_text.db"
        @Volatile private var instance: RepeatedTextDatabase? = null
        private val Lock = Any()

        operator fun invoke(context: Context) = instance ?: synchronized(Lock) {
            instance ?: buildDatabase(context).also { instance = it }
        }

        private fun buildDatabase(context: Context) = Room.databaseBuilder(context,
            RepeatedTextDatabase::class.java, REPEATED_TEXT_APP_DATABASE_NAME)
            .build()
    }
}