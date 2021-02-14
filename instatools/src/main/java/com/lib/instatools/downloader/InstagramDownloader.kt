package com.lib.instatools.downloader

import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.lib.instatools.model.DownloadData
import com.lib.instatools.model.DownloadType
import com.lib.instatools.model.ResultData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.jsoup.Jsoup

const val USER_AGENT = "Mozilla/5.0"
const val META_TAG = "meta"
const val PROPERTY = "property"
const val CONTENT = "content"
const val PROPERTY_TYPE_CHECK_IMAGE = "og:image"
const val PROPERTY_TYPE_CHECK_VIDEO = "og:video"
const val PROPERTY_TYPE_CHECK_ANY = "og:type"

const val PROPERTY_TYPE_PHOTO = "instapp:photo"
const val PROPERTY_TYPE_VIDEO = "video"
const val PROPERTY_TYPE_PROFILE = "profile"

class InstagramDownloader(private val coroutineScope: LifecycleCoroutineScope) {
    private val _responseData: MutableLiveData<ResultData<DownloadData>> = MutableLiveData()
    val responseData: LiveData<ResultData<DownloadData>> get() = _responseData

    fun download(link: String) = this.apply {
        coroutineScope.launch(Dispatchers.IO) {
            runCatching {
                downloadMedia(link = link)
            }.onFailure {
                _responseData.postValue(ResultData.Failure(message = "No data found"))
            }
        }
    }

    private fun downloadMedia(link: String) {
        val document = Jsoup.connect(link).userAgent(USER_AGENT).get()
        val metas = document.getElementsByTag(META_TAG)
        var imageUrl = String()
        var videoUrl = String()
        var mediaType = String()
        for (meta in metas) {
            when (meta.attr(PROPERTY)) {
                PROPERTY_TYPE_CHECK_IMAGE -> {
                    imageUrl = meta.attr(CONTENT)
                }
                PROPERTY_TYPE_CHECK_VIDEO -> {
                    videoUrl = meta.attr(CONTENT)
                }
                PROPERTY_TYPE_CHECK_ANY -> {
                    mediaType = meta.attr(CONTENT)
                }
            }
        }

        val downloadType =
            if (mediaType != PROPERTY_TYPE_PHOTO && mediaType != PROPERTY_TYPE_VIDEO && mediaType != PROPERTY_TYPE_PROFILE) {
                DownloadType.TYPE_INVALID
            } else {
                when (mediaType) {
                    PROPERTY_TYPE_PHOTO -> {
                        when {
                            imageUrl.isNotEmpty() -> DownloadType.TYPE_IMAGE
                            else -> DownloadType.TYPE_INVALID
                        }
                    }
                    PROPERTY_TYPE_VIDEO -> {
                        when {
                            videoUrl.isNotEmpty() -> DownloadType.TYPE_VIDEO
                            else -> DownloadType.TYPE_INVALID
                        }
                    }
                    PROPERTY_TYPE_PROFILE -> {
                        when {
                            imageUrl.isNotEmpty() -> DownloadType.TYPE_DP
                            else -> DownloadType.TYPE_INVALID
                        }
                    }
                    else -> {
                        DownloadType.TYPE_INVALID
                    }
                }
            }
        when (downloadType) {
            DownloadType.TYPE_IMAGE -> {
                _responseData.postValue(
                    ResultData.Success(
                        DownloadData(
                            fileUrl = imageUrl,
                            DownloadType.TYPE_IMAGE
                        )
                    )
                )
            }
            DownloadType.TYPE_VIDEO -> {
                _responseData.postValue(
                    ResultData.Success(
                        DownloadData(
                            fileUrl = videoUrl,
                            DownloadType.TYPE_VIDEO
                        )
                    )
                )
            }
            DownloadType.TYPE_DP -> {
                _responseData.postValue(
                    ResultData.Success(
                        DownloadData(
                            fileUrl = imageUrl,
                            DownloadType.TYPE_DP
                        )
                    )
                )
            }
            else -> {
                _responseData.postValue(ResultData.Failure(message = "No data found"))
            }
        }
    }
}