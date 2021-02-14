package com.app.sociallibs

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatTextView
import androidx.lifecycle.MutableLiveData
import com.lib.textrepeater.callback.TextRepeaterServiceCallback
import com.lib.textrepeater.repeater.TextRepeater

class TextRepeater : AppCompatActivity() {
    lateinit var textRepeater: TextRepeater

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_text_repeater)

        findViewById<AppCompatButton>(R.id.button_repeat).setOnClickListener {
            val text = findViewById<AppCompatEditText>(R.id.edit_text_message).text.toString()
            textRepeater.repeatText(text, 20000, false, true)
        }

        textRepeater = TextRepeater.Builder()
            .setActivity(this)
            .setTextRepeaterServiceCallback(object: TextRepeaterServiceCallback {
                override fun textRepeatingListener(repeatedTextLiveData: MutableLiveData<String>) {
                    repeatedTextLiveData.observe(this@TextRepeater) { repeatedText ->
                        findViewById<AppCompatTextView>(R.id.text_repeatedtext).text = repeatedText
                    }
                }
            })
            .build()
    }

    override fun onDestroy() {
        super.onDestroy()
        textRepeater.destroy()
    }
}