package com.lib.whatsapptools

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.ContextCompat.startActivity

class WhatsappTools(builder: Builder) {
    private val context: Context = builder.context

    fun sendMessage(fullPhoneNumber: String, message: String = String()) {
        val uri: Uri =
            Uri.parse(context.getString(R.string.whatsapp_url, fullPhoneNumber, message))
        val sendIntent = Intent(Intent.ACTION_VIEW, uri)
        startActivity(context, sendIntent, null)
    }

    class Builder(context: Context) {
        var context: Context = context
            private set

        fun build() = WhatsappTools(this)
    }
}