package com.example.tahfidzdoaquplay

import android.app.Application
import com.google.android.exoplayer2.DefaultRenderersFactory
import com.google.android.exoplayer2.DefaultRenderersFactory.ExtensionRendererMode
import com.google.android.exoplayer2.RenderersFactory
import com.google.android.exoplayer2.database.DatabaseProvider
import com.google.android.exoplayer2.database.ExoDatabaseProvider
import com.google.android.exoplayer2.offline.*
import com.google.android.exoplayer2.upstream.*
import com.google.android.exoplayer2.upstream.cache.*
import com.google.android.exoplayer2.util.Log
import com.google.android.exoplayer2.util.Util
import java.io.File
import java.io.IOException


/**
 * Placeholder application to facilitate overriding Application methods for debugging and testing.
 */
class DemoApplication : Application() {
    protected var userAgent: String? = null
    private var databaseProvider: DatabaseProvider? = null
        private get() {
            if (field == null) {
                field = ExoDatabaseProvider(this)
            }
            return field
        }
    private var downloadDirectory: File? = null
        private get() {
            if (field == null) {
                field = getExternalFilesDir(null)
                if (field == null) {
                    field = filesDir
                }
            }
            return field
        }
    @get:Synchronized
    protected var downloadCache: Cache? = null
        protected get() {
            if (field == null) {
                val downloadContentDirectory = File(
                    downloadDirectory,
                    DOWNLOAD_CONTENT_DIRECTORY
                )
                field =
                    SimpleCache(downloadContentDirectory, NoOpCacheEvictor(), databaseProvider)
            }
            return field
        }
        private set
    private var downloadManager: DownloadManager? = null
    private var downloadTracker: DownloadTracker? = null
    override fun onCreate() {
        super.onCreate()
        userAgent = Util.getUserAgent(this, "ExoPlayerDemo")
    }

    /** Returns a [DataSource.Factory].  */
    fun buildDataSourceFactory(): DataSource.Factory {
        val upstreamFactory =
            DefaultDataSourceFactory(this, buildHttpDataSourceFactory())
        return buildReadOnlyCacheDataSource(
            upstreamFactory,
            downloadCache
        )
    }

    /** Returns a [HttpDataSource.Factory].  */
    fun buildHttpDataSourceFactory(): HttpDataSource.Factory {
        return DefaultHttpDataSourceFactory(userAgent)
    }

    /** Returns whether extension renderers should be used.  */
    fun useExtensionRenderers(): Boolean {
        return "withExtensions" == BuildConfig.FLAVOR
    }

    fun buildRenderersFactory(preferExtensionRenderer: Boolean): RenderersFactory {
        @ExtensionRendererMode val extensionRendererMode =
            if (useExtensionRenderers()) if (preferExtensionRenderer) DefaultRenderersFactory.EXTENSION_RENDERER_MODE_PREFER else DefaultRenderersFactory.EXTENSION_RENDERER_MODE_ON else DefaultRenderersFactory.EXTENSION_RENDERER_MODE_OFF
        return DefaultRenderersFactory( /* context= */this)
            .setExtensionRendererMode(extensionRendererMode)
    }

    fun getDownloadManager(): DownloadManager? {
        initDownloadManager()
        return downloadManager
    }

    fun getDownloadTracker(): DownloadTracker? {
        initDownloadManager()
        return downloadTracker
    }

    @Synchronized
    private fun initDownloadManager() {
        if (downloadManager == null) {
            val downloadIndex = DefaultDownloadIndex(databaseProvider)
            upgradeActionFile(
                DOWNLOAD_ACTION_FILE,
                downloadIndex,  /* addNewDownloadsAsCompleted= */
                false
            )
            upgradeActionFile(
                DOWNLOAD_TRACKER_ACTION_FILE,
                downloadIndex,  /* addNewDownloadsAsCompleted= */
                true
            )
            val downloaderConstructorHelper =
                DownloaderConstructorHelper(downloadCache, buildHttpDataSourceFactory())
            downloadManager = DownloadManager(
                this, downloadIndex, DefaultDownloaderFactory(downloaderConstructorHelper)
            )
            downloadTracker =
                DownloadTracker( /* context= */this, buildDataSourceFactory(), downloadManager!!)
        }
    }

    private fun upgradeActionFile(
        fileName: String,
        downloadIndex: DefaultDownloadIndex,
        addNewDownloadsAsCompleted: Boolean
    ) {
        try {
            ActionFileUpgradeUtil.upgradeAndDelete(
                File(downloadDirectory, fileName),  /* downloadIdProvider= */
                null,
                downloadIndex,  /* deleteOnFailure= */
                true,
                addNewDownloadsAsCompleted
            )
        } catch (e: IOException) {
            Log.e(
                TAG,
                "Failed to upgrade action file: $fileName",
                e
            )
        }
    }

    companion object {
        private const val TAG = "DemoApplication"
        private const val DOWNLOAD_ACTION_FILE = "actions"
        private const val DOWNLOAD_TRACKER_ACTION_FILE = "tracked_actions"
        private const val DOWNLOAD_CONTENT_DIRECTORY = "downloads"
        protected fun buildReadOnlyCacheDataSource(
            upstreamFactory: DataSource.Factory?,
            cache: Cache?
        ): CacheDataSourceFactory {
            return CacheDataSourceFactory(
                cache,
                upstreamFactory,
                FileDataSourceFactory(),  /* cacheWriteDataSinkFactory= */
                null,
                CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR,  /* eventListener= */
                null
            )
        }
    }
}