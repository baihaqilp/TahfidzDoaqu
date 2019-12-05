package com.example.tahfidzdoaquplay

import android.app.Activity
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import com.google.android.exoplayer2.DefaultRenderersFactory
import com.google.android.exoplayer2.DefaultRenderersFactory.ExtensionRendererMode
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.RenderersFactory
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.database.DatabaseProvider
import com.google.android.exoplayer2.database.ExoDatabaseProvider
import com.google.android.exoplayer2.offline.*
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.*
import com.google.android.exoplayer2.upstream.cache.*
import com.google.android.exoplayer2.util.Log
import com.google.android.exoplayer2.util.Util
import kotlinx.android.synthetic.main.activity_abasa.*
import java.io.File
import java.io.IOException


class AbasaActivity : Activity() {
    private lateinit var player: SimpleExoPlayer
    private lateinit var mediaDataSourceFactory: DataSource.Factory

    private val TAG = "DemoApplication"
    private val DOWNLOAD_ACTION_FILE = "actions"
    private val DOWNLOAD_TRACKER_ACTION_FILE = "tracked_actions"
    private val DOWNLOAD_CONTENT_DIRECTORY = "downloads"

    protected var userAgent: String? = null

    private var databaseProvider: DatabaseProvider? = null
    private var downloadDirectory: File? = null
    private var downloadCache: Cache? = null
    private lateinit var downloadManager: DownloadManager
    private lateinit var downloadTracker: DownloadTracker


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_abasa)
        userAgent = Util.getUserAgent(this, "ExoPlayerDemo");
    }

    private fun initializePlayer() {

        player = ExoPlayerFactory.newSimpleInstance(this)

        mediaDataSourceFactory = DefaultDataSourceFactory(this, Util.getUserAgent(this, "mediaPlayerSample"))

        val mediaSource = ProgressiveMediaSource.Factory(mediaDataSourceFactory)
            .createMediaSource(Uri.parse(STREAM_URL))


        with(player) {
            prepare(mediaSource, false, false)
            playWhenReady = true
        }


        playerView.setShutterBackgroundColor(Color.TRANSPARENT)
        playerView.player = player
        playerView.requestFocus()

    }



    private fun releasePlayer() {
        player.release()
    }

    public override fun onStart() {
        super.onStart()

        if (Util.SDK_INT > 23) initializePlayer()
    }

    public override fun onResume() {
        super.onResume()

        if (Util.SDK_INT <= 23) initializePlayer()
    }

    public override fun onPause() {
        super.onPause()

        if (Util.SDK_INT <= 23) releasePlayer()
    }

    public override fun onStop() {
        super.onStop()

        if (Util.SDK_INT > 23) releasePlayer()
    }

    companion object {
        const val STREAM_URL = "http://firdaus.tahfidzta.doaqu.or.id/api/surat/Empat_Mata.mp3"
    }

    /** Returns a [DataSource.Factory].  */
    fun buildDataSourceFactory(): DataSource.Factory? {
        val upstreamFactory =
            DefaultDataSourceFactory(this, buildHttpDataSourceFactory())
        return buildReadOnlyCacheDataSource(upstreamFactory, getDownloadCache())
    }

    /** Returns a [HttpDataSource.Factory].  */
    fun buildHttpDataSourceFactory(): HttpDataSource.Factory? {
        return DefaultHttpDataSourceFactory(userAgent)
    }

    /** Returns whether extension renderers should be used.  */
    fun useExtensionRenderers(): Boolean {
        return "withExtensions" == BuildConfig.FLAVOR
    }

    fun buildRenderersFactory(preferExtensionRenderer: Boolean): RenderersFactory? {
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
    protected fun getDownloadCache(): Cache? {
        if (downloadCache == null) {
            val downloadContentDirectory =
                File(getDownloadDirectory(), DOWNLOAD_CONTENT_DIRECTORY)
            downloadCache =
                SimpleCache(downloadContentDirectory, NoOpCacheEvictor(), getDatabaseProvider())
        }
        return downloadCache
    }

    @Synchronized
    private fun initDownloadManager() {
        if (downloadManager == null) {
            val downloadIndex = DefaultDownloadIndex(getDatabaseProvider())
            upgradeActionFile(
                DOWNLOAD_ACTION_FILE, downloadIndex,  /* addNewDownloadsAsCompleted= */false
            )
            upgradeActionFile(
                DOWNLOAD_TRACKER_ACTION_FILE, downloadIndex,  /* addNewDownloadsAsCompleted= */true
            )
            val downloaderConstructorHelper =
                DownloaderConstructorHelper(getDownloadCache(), buildHttpDataSourceFactory())
            downloadManager = DownloadManager(
                this, downloadIndex, DefaultDownloaderFactory(downloaderConstructorHelper)
            )
            downloadTracker = DownloadTracker(this,buildDataSourceFactory(),downloadManager)

        }
    }

    private fun upgradeActionFile(
        fileName: String,
        downloadIndex: DefaultDownloadIndex,
        addNewDownloadsAsCompleted: Boolean
    ) {
        try {
            ActionFileUpgradeUtil.upgradeAndDelete(
                File(getDownloadDirectory(), fileName),  /* downloadIdProvider= */
                null,
                downloadIndex,  /* deleteOnFailure= */
                true,
                addNewDownloadsAsCompleted
            )
        } catch (e: IOException) {
            Log.e(TAG, "Failed to upgrade action file: $fileName", e)
        }
    }

    private fun getDatabaseProvider(): DatabaseProvider? {
        if (databaseProvider == null) {
            databaseProvider = ExoDatabaseProvider(this)
        }
        return databaseProvider
    }

    private fun getDownloadDirectory(): File? {
        if (downloadDirectory == null) {
            downloadDirectory = getExternalFilesDir(null)
            if (downloadDirectory == null) {
                downloadDirectory = filesDir
            }
        }
        return downloadDirectory
    }

    protected fun buildReadOnlyCacheDataSource(
        upstreamFactory: DataSource.Factory?,
        cache: Cache?
    ): CacheDataSourceFactory? {
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
