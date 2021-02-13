package com.lib.textrepeater.repeater

import android.content.ComponentName
import android.content.Context
import android.content.ServiceConnection
import android.os.IBinder
import androidx.lifecycle.LiveData
import com.lib.database.dao.RepeatedTextDao
import com.lib.database.database.RepeatedTextDatabase
import com.lib.database.entitity.RepeatedTextEntity
import com.lib.textrepeater.callback.TextRepeaterServiceCallback
import com.lib.textrepeater.service.RepeatTextForegroundService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class TextRepeater(builder: Builder) {
    private var isBound: Boolean = false
    private val context: Context = builder.context
    private val textRepeaterServiceCallback: TextRepeaterServiceCallback? = builder.textRepeaterServiceCallback
    private val textToRepeat: String = builder.textToRepeat
    private val repeatLimit: Int = builder.repeatLimit
    private val addSpaceEnabled: Boolean = builder.addSpaceEnabled
    private val newLineEnabled: Boolean = builder.newLineEnabled
    private val repeatedTextDatabase: RepeatedTextDatabase = RepeatedTextDatabase.invoke(context)
    private val repeatedTextDao: RepeatedTextDao = repeatedTextDatabase.repeatedTextDao()
    private val backgroundJobs = mutableListOf<Job>()

    private fun launchBackgroundTask(task: suspend () -> Unit) {
        val job = GlobalScope.launch(Dispatchers.Default) {
            task()
        }
        backgroundJobs.add(job)
    }

    private fun insertTextToDb(text: String) {
        launchBackgroundTask {
            repeatedTextDao.insertRepeatedText(RepeatedTextEntity(recentTextToRepeat = text))
        }
    }

    fun getHistoryOfTexts(): LiveData<MutableList<RepeatedTextEntity>> {
        return repeatedTextDao.getAllRecentTexts()
    }

    fun deleteTextFromHistory(text: String) {
        launchBackgroundTask {
            repeatedTextDao.deleteRepeatedText(text)
        }
    }

    fun deleteAllTextHistory() {
        launchBackgroundTask {
            repeatedTextDatabase.clearAllTables()
        }
    }

    fun repeatText() {
        insertTextToDb(text = textToRepeat)

        RepeatTextForegroundService.startService(
            context,
            textToRepeat, repeatLimit, addSpaceEnabled, newLineEnabled, myConnection
        )
    }

    fun destroy() {
        backgroundJobs.forEach { backgroundJobs ->
            backgroundJobs.cancel()
        }
        RepeatTextForegroundService.stopService(context, myConnection)
    }

    private val myConnection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName,
                                        service: IBinder
        ) {
            val binder = service as RepeatTextForegroundService.MyLocalBinder
            val myService = binder.getService()
            isBound = true

            textRepeaterServiceCallback?.textRepeatingListener(myService.repeatedTextLiveData)
        }

        override fun onServiceDisconnected(name: ComponentName) {
            isBound = false
        }
    }

    class Builder(context: Context) {
        var context = context
            private set

        var textRepeaterServiceCallback: TextRepeaterServiceCallback? = null
            private set

        var textToRepeat: String = String()
            private set

        var repeatLimit: Int = 1
            private set

        var addSpaceEnabled: Boolean = false
            private set

        var newLineEnabled: Boolean = false
            private set

        fun setTextRepeaterServiceCallback(textRepeaterServiceCallback: TextRepeaterServiceCallback) = apply {
            this.textRepeaterServiceCallback = textRepeaterServiceCallback
        }

        fun setTextToRepeat(textToRepeat: String) = apply {
            this.textToRepeat = textToRepeat
        }

        fun setRepeatLimit(repeatLimit: Int) = apply {
            this.repeatLimit = repeatLimit
        }

        fun setAddSpaceEnabled(addSpaceEnabled: Boolean) = apply {
            this.addSpaceEnabled = addSpaceEnabled
        }

        fun setNewLineEnabled(newLineEnabled: Boolean) = apply {
            this.newLineEnabled = newLineEnabled
        }

        fun build() = TextRepeater(this)
    }
}