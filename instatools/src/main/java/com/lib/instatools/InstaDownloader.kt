package com.lib.instatools

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.LiveData
import androidx.lifecycle.lifecycleScope
import com.lib.instatools.downloader.InstagramDownloader
import com.lib.instatools.model.DownloadData
import com.lib.instatools.model.ResultData

class InstaDownloader(builder: Builder) {
    private val activity: FragmentActivity? = builder.activity
    private val fragment: Fragment? = builder.fragment

    fun download(link: String): LiveData<ResultData<DownloadData>> {
        return InstagramDownloader(coroutineScope)
            .download(link = link)
            .responseData
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

        fun build() = InstaDownloader(this)
    }
}