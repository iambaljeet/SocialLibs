package com.lib.textrepeater.service

import android.app.*
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Binder
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.lifecycle.MutableLiveData
import com.lib.textrepeater.R
import kotlinx.coroutines.*

class RepeatTextForegroundService : Service() {

    private val NOTIFICATION_ID = 125
    private lateinit var NOTIFICATION_CHANNEL_ID : String
    private lateinit var NOTIFICATION_CHANNEL_NAME: String

    private var coroutineJob: Job? = null
    private val myBinder = MyLocalBinder()
    val repeatedTextLiveData: MutableLiveData<String> = MutableLiveData()

    inner class MyLocalBinder : Binder() {
        fun getService() : RepeatTextForegroundService {
            return this@RepeatTextForegroundService
        }
    }

    companion object {
        const val TEXT_TO_REPEAT = "TEXT_TO_REPEAT"
        const val REPEAT_LIMIT = "REPEAT_LIMIT"
        const val NEW_LINE_ENABLED = "NEW_LINE_ENABLED"
        const val ADD_SPACE_ENABLED = "ADD_SPACE_ENABLED"

        fun startService(
            context: Context,
            textToRepeat: String,
            repeatLimit: Int,
            addSpaceEnabled: Boolean,
            newLineEnabled: Boolean,
            myConnection: ServiceConnection
        ) {
            val startIntent = Intent(context, RepeatTextForegroundService::class.java)
            startIntent.putExtra(TEXT_TO_REPEAT, textToRepeat)
            startIntent.putExtra(REPEAT_LIMIT, repeatLimit)
            startIntent.putExtra(ADD_SPACE_ENABLED, addSpaceEnabled)
            startIntent.putExtra(NEW_LINE_ENABLED, newLineEnabled)
            context.startService(startIntent)
            context.bindService(startIntent, myConnection, Context.BIND_AUTO_CREATE)
        }

        fun stopService(
            context: Context,
            myConnection: ServiceConnection
        ) {
            val stopIntent = Intent(context, RepeatTextForegroundService::class.java)
            context.stopService(stopIntent)
            context.unbindService(myConnection)
        }
    }

    override fun onCreate() {
        super.onCreate()
        NOTIFICATION_CHANNEL_ID = getString(R.string.repeating_text_notification_channel_id)
        NOTIFICATION_CHANNEL_NAME = getString(R.string.tepeating_text_notification_channel_name)
    }

    override fun onBind(intent: Intent?): IBinder {
        return myBinder
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val startForegroundServiceNotification = startForegroundServiceNotification()

        startForeground(NOTIFICATION_ID, startForegroundServiceNotification)

        startRepeatingText(intent)

        return START_NOT_STICKY
    }

    private fun startForegroundServiceNotification(): Notification? {
        createNotificationChannel()

        return NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setContentTitle(getString(R.string.repeat_notification_title))
            .setContentText(getString(R.string.repeat_notification_description))
            .setSmallIcon(R.drawable.ic_repeat_text_notification)
            .build()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(NOTIFICATION_CHANNEL_ID, NOTIFICATION_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT)
            val manager = getSystemService(NotificationManager::class.java)
            manager?.createNotificationChannel(serviceChannel)
        }
    }

    private fun startRepeatingText(intent: Intent?) {
        val textToRepeat = intent?.getStringExtra(TEXT_TO_REPEAT) ?: ""
        val repeatLimit = intent?.getIntExtra(REPEAT_LIMIT, 1) ?: 1
        val addSpaceEnabled = intent?.getBooleanExtra(ADD_SPACE_ENABLED, false) ?: false
        val newLineEnabled = intent?.getBooleanExtra(NEW_LINE_ENABLED, false) ?: false

        coroutineJob = CoroutineScope(Dispatchers.IO).launch {
            val repeatedText: String =
                repeatAndReturnText(
                    textToRepeat,
                    repeatLimit,
                    addSpaceEnabled,
                    newLineEnabled
                )
            repeatedTextLiveData.postValue(repeatedText)
            withContext(Dispatchers.Main) {
                stopForeground(true)
            }
        }
    }

    private fun repeatAndReturnText(
        textToRepeat: String,
        repeatLimit: Int,
        addSpaceEnabled: Boolean,
        newLineEnabled: Boolean
    ): String {
        var repeatedText = ""
        if (newLineEnabled && addSpaceEnabled) {
            try {
                repeatedText = "$textToRepeat \n".repeat(repeatLimit)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else if (!newLineEnabled && !addSpaceEnabled) {
            try {
                repeatedText = textToRepeat.repeat(repeatLimit)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        if (newLineEnabled && !addSpaceEnabled) {
            try {
                repeatedText = "$textToRepeat\n".repeat(repeatLimit)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        if (!newLineEnabled && addSpaceEnabled) {
            try {
                repeatedText = "$textToRepeat ".repeat(repeatLimit)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return repeatedText
    }

    override fun onDestroy() {
        coroutineJob?.cancel()
        super.onDestroy()
    }
}