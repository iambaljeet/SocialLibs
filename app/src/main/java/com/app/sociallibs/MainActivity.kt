package com.app.sociallibs

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import com.lib.textrepeater.repeater.TextRepeater

private const val TAG = "MainActivity"
class MainActivity : AppCompatActivity() {
    lateinit var textRepeater: TextRepeater

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<AppCompatButton>(R.id.button_instatools)
                .setOnClickListener {
                    startActivity(Intent(this, InstaTools::class.java))
                }

//        textRepeater = TextRepeater.Builder(this)
//            .setTextToRepeat("How are you")
//            .setRepeatLimit(200000)
//            .setAddSpaceEnabled(false)
//            .setNewLineEnabled(true)
//            .setTextRepeaterServiceCallback(object: TextRepeaterServiceCallback {
//                override fun textRepeatingListener(repeatedTextLiveData: MutableLiveData<String>) {
//                    repeatedTextLiveData.observe(this@MainActivity) { repeatedText ->
//                        findViewById<TextView>(R.id.text_view_repeated_text).apply {
//                            text = repeatedText
//                        }
//                    }
//                }
//            })
//            .build()
//
//        textRepeater.repeatText()
//
//        textRepeater.getHistoryOfTexts().observe(this) { repeatedText ->
//            Log.d(TAG, "getHistoryOfTexts: repeatedText: $repeatedText")
//        }
    }

    override fun onDestroy() {
        super.onDestroy()
//        textRepeater.destroy()
    }
}