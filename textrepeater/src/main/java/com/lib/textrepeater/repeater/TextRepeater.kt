package com.lib.textrepeater.repeater

import android.content.ComponentName
import android.content.Context
import android.content.ServiceConnection
import android.os.IBinder
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.LiveData
import androidx.lifecycle.lifecycleScope
import com.lib.textrepeater.callback.TextRepeaterServiceCallback
import com.lib.textrepeater.database.dao.RepeatedTextDao
import com.lib.textrepeater.database.database.RepeatedTextDatabase
import com.lib.textrepeater.database.entitity.RepeatedTextEntity
import com.lib.textrepeater.service.RepeatTextForegroundService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TextRepeater(builder: Builder) {
    private var isBound: Boolean = false
    private val activity: FragmentActivity? = builder.activity
    private val fragment: Fragment? = builder.fragment
    private val context: Context? = when {
        activity != null -> {
            activity
        }
        fragment != null -> {
            fragment.context
        }
        else -> {
            throw Exception("Activity and fragment is null")
        }
    }

    private val coroutineScope: LifecycleCoroutineScope = when {
        activity != null -> {
            activity.lifecycleScope
        }
        fragment != null -> {
            fragment.viewLifecycleOwner.lifecycleScope
        }
        else -> {
            throw Exception("Activity and fragment is null")
        }
    }

    private val textRepeaterServiceCallback: TextRepeaterServiceCallback? = builder.textRepeaterServiceCallback
    private val repeatedTextDatabase: RepeatedTextDatabase? = RepeatedTextDatabase.invoke(context)
    private val repeatedTextDao: RepeatedTextDao? = repeatedTextDatabase?.repeatedTextDao()

    private fun insertTextToDb(text: String) {
        coroutineScope.launch(Dispatchers.IO) {
            repeatedTextDao?.insertRepeatedText(RepeatedTextEntity(recentTextToRepeat = text))
        }
    }

    fun getHistoryOfTexts(): LiveData<MutableList<RepeatedTextEntity>>? {
        return repeatedTextDao?.getAllRecentTexts()
    }

    fun deleteTextFromHistory(text: String) {
        coroutineScope.launch(Dispatchers.IO) {
            repeatedTextDao?.deleteRepeatedText(text)
        }
    }

    fun deleteAllTextHistory() {
        coroutineScope.launch(Dispatchers.IO) {
            repeatedTextDatabase?.clearAllTables()
        }
    }

    fun repeatText(textToRepeat: String, repeatLimit: Int, addSpaceEnabled: Boolean, newLineEnabled: Boolean) {
        insertTextToDb(text = textToRepeat)

        context?.let { context ->
            RepeatTextForegroundService.startService(
                context,
                textToRepeat, repeatLimit, addSpaceEnabled, newLineEnabled, myConnection
            )
        }
    }

    fun destroy() {
        context?.let { context -> RepeatTextForegroundService.stopService(context, myConnection) }
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

    class Builder {
        var activity: FragmentActivity? = null
            private set

        var fragment: Fragment? = null
            private set

        fun setActivity(activity: FragmentActivity) = this.apply {
            this.activity = activity
        }

        fun setFragment(fragment: Fragment) = this.apply {
            this.fragment = fragment
        }

        var textRepeaterServiceCallback: TextRepeaterServiceCallback? = null
            private set

        fun setTextRepeaterServiceCallback(textRepeaterServiceCallback: TextRepeaterServiceCallback) = apply {
            this.textRepeaterServiceCallback = textRepeaterServiceCallback
        }

        fun build() = TextRepeater(this)
    }
}