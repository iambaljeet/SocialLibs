package com.lib.textrepeater.callback

import androidx.lifecycle.MutableLiveData

interface TextRepeaterServiceCallback {
    fun textRepeatingListener(repeatedTextLiveData: MutableLiveData<String>)
}