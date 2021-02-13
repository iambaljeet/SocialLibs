package com.app.sociallibs

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import com.lib.textrepeater.callback.TextRepeaterServiceCallback
import com.lib.textrepeater.repeater.TextRepeater

private const val TAG = "MainActivity"
class MainActivity : AppCompatActivity() {
    lateinit var textRepeater: TextRepeater

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        textRepeater = TextRepeater.Builder(this)
            .setTextToRepeat("How are you")
            .setRepeatLimit(200000)
            .setAddSpaceEnabled(false)
            .setNewLineEnabled(true)
            .setTextRepeaterServiceCallback(object: TextRepeaterServiceCallback {
                override fun textRepeatingListener(repeatedTextLiveData: MutableLiveData<String>) {
                    repeatedTextLiveData.observe(this@MainActivity) { repeatedText ->
                        findViewById<TextView>(R.id.text_view_repeated_text).apply {
                            text = repeatedText
                        }
                    }
                }
            })
            .build()

        textRepeater.repeatText()

        textRepeater.getHistoryOfTexts().observe(this) { repeatedText ->
            Log.d(TAG, "getHistoryOfTexts: repeatedText: $repeatedText")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        textRepeater.destroy()
    }
}