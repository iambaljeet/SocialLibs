package com.app.sociallibs

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton

private const val TAG = "MainActivity"
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<AppCompatButton>(R.id.button_instatools)
                .setOnClickListener {
                    startActivity(Intent(this, InstaTools::class.java))
                }

        findViewById<AppCompatButton>(R.id.button_textrepeater)
            .setOnClickListener {
                startActivity(Intent(this, TextRepeater::class.java))
            }
    }
}