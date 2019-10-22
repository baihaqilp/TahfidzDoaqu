package com.example.tahfidzdoaquplay

import android.content.Context
import android.net.Uri
import androidx.annotation.Nullable
import com.google.android.exoplayer2.offline.DownloadIndex
import com.google.android.exoplayer2.offline.DownloadHelper
import com.google.android.exoplayer2.offline.Download
import com.google.android.exoplayer2.offline.DownloadManager
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import java.util.concurrent.CopyOnWriteArraySet
import com.google.android.exoplayer2.offline.Download




class DownloadTracker {

    interface Listener {

        /** Called when the tracked downloads changed.  */
        fun onDownloadsChanged()
    }

    private val TAG = "DownloadTracker"

    private val context: Context? = null
    private val dataSourceFactory: DataSource.Factory? = null
    private var listeners: CopyOnWriteArraySet<Listener>? = null
    private var downloads: HashMap<Uri, Download>? = null
    private var downloadIndex: DownloadIndex? = null

    fun DownloadTracker(
        context: Context, dataSourceFactory: DataSource.Factory, downloadManager: DownloadManager
    ){
        this.context = context.applicationContext
        this.dataSourceFactory = dataSourceFactory
        listeners = CopyOnWriteArraySet()
        downloads = HashMap()
        downloadIndex = downloadManager.downloadIndex
        downloadManager.addListener(DownloadManagerListener())
        loadDownloads()
    }


}