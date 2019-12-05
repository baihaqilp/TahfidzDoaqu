package com.example.tahfidzdoaquplay

import android.R
import android.app.Notification
import com.google.android.exoplayer2.offline.Download
import com.google.android.exoplayer2.offline.DownloadManager
import com.google.android.exoplayer2.offline.DownloadService
import com.google.android.exoplayer2.scheduler.PlatformScheduler
import com.google.android.exoplayer2.ui.DownloadNotificationHelper
import com.google.android.exoplayer2.util.NotificationUtil
import com.google.android.exoplayer2.util.Util


internal class TahfizDownloadService : DownloadService(1){

    private val CHANNEL_ID:String = "download_channel"
    private val JOB_ID:Int = 1
    private val FOREGROUND_NOTIFICATION_ID=1

    private var nextNotificationId :Int = FOREGROUND_NOTIFICATION_ID  +1

    private lateinit var notificationHelper: DownloadNotificationHelper



    fun OnCreate(){
        super.onCreate();
    }


    override fun getDownloadManager(): DownloadManager? {
        return (application as DemoApplication).getDownloadManager()
    }

    override fun getScheduler(): PlatformScheduler? {
        return if (Util.SDK_INT >= 21) PlatformScheduler(this, JOB_ID) else null
    }

    override fun getForegroundNotification(downloads: List<Download?>?): Notification? {
        return notificationHelper.buildProgressNotification(
            R.drawable.ic_d,  /* contentIntent= */null,  /* message= */null, downloads
        )
    }

    override fun onDownloadChanged(download: Download) {
        val notification: Notification
        notification = if (download.state == Download.STATE_COMPLETED) {
            notificationHelper.buildDownloadCompletedNotification(
                R.drawable.ic_download_done,  /* contentIntent= */
                null,
                Util.fromUtf8Bytes(download.request.data)
            )
        } else if (download.state == Download.STATE_FAILED) {
            notificationHelper.buildDownloadFailedNotification(
                R.drawable.ic_download_done,  /* contentIntent= */
                null,
                Util.fromUtf8Bytes(download.request.data)
            )
        } else {
            return
        }
        NotificationUtil.setNotification(this, nextNotificationId++, notification)
    }
}