package com.app.sociallibs

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatTextView
import com.lib.instatools.InstaDownloader
import com.lib.instatools.model.ResultData

class InstaTools : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_insta_tools)

        val instaDownloader = InstaDownloader.Builder()
            .setActivity(this)
            .build()

        findViewById<AppCompatButton>(R.id.button_fetch).setOnClickListener {
            val links = findViewById<AppCompatEditText>(R.id.edit_text_link).text.toString()

            instaDownloader.download(links).observe(this) { result ->
                when(result) {
                    is ResultData.Success -> {
                        findViewById<AppCompatTextView>(R.id.text_logs).text = result.data.toString()
                    }
                    is ResultData.Failure -> {
                        findViewById<AppCompatTextView>(R.id.text_logs).text = result.toString()
                    }
                }
            }
        }
    }
}