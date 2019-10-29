package com.example.tahfidzdoaquplay

import android.app.Application
import com.google.android.exoplayer2.database.DatabaseProvider
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import com.google.android.exoplayer2.offline.DownloadManager
import com.google.android.exoplayer2.upstream.cache.Cache
import java.io.File


class DemoTahfiz : Application(){
    private val TAG = "DemoApplication"
    private val DOWNLOAD_ACTION_FILE = "actions"
    private val DOWNLOAD_TRACKER_ACTION_FILE = "tracked_actions"
    private val DOWNLOAD_CONTENT_DIRECTORY = "downloads"

    protected var userAgent: String? = null

    private val databaseProvider: DatabaseProvider? = null
    private val downloadDirectory: File? = null
    private val downloadCache: Cache? = null
    private val downloadManager: DownloadManager? = null
}
