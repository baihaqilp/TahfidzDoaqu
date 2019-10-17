package com.example.tahfidzdoaquplay

import android.app.Notification
import com.google.android.exoplayer2.offline.Download
import com.google.android.exoplayer2.offline.DownloadService
import com.google.android.exoplayer2.offline.DownloadManager
import com.google.android.exoplayer2.scheduler.PlatformScheduler;
import com.google.android.exoplayer2.scheduler.Scheduler
import com.google.android.exoplayer2.ui.DownloadNotificationHelper;
import com.google.android.exoplayer2.util.NotificationUtil;
import com.google.android.exoplayer2.util.Util;
import java.nio.channels.Channel

class TahfizDownloadService : DownloadService(1){

    private val CHANNEL_ID:String = "download_channel"
    private val JOB_ID:Int = 1
    private val FOREGROUND_NOTIFICATION_ID=1

    private var nextNotificationId :Int = FOREGROUND_NOTIFICATION_ID  +1

    private lateinit var notificationHelper: DownloadNotificationHelper


    fun TahfizDownloadService() {

    }



    override fun getDownloadManager(): DownloadManager {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getForegroundNotification(downloads: MutableList<Download>?): Notification {
        TODO()
    }

    override fun getScheduler(): Scheduler? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}