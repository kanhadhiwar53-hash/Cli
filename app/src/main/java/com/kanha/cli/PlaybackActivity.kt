package com.kanha.cli

import android.app.Activity
import android.os.Bundle
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import java.net.URL

/** Plays a direct HTTPS stream supplied by an authorized backend. */
class PlaybackActivity : Activity() {
    private var player: ExoPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val url = intent.getStringExtra(EXTRA_URL).orEmpty()
        val title = intent.getStringExtra(EXTRA_TITLE).orEmpty()
        val parsed = runCatching { URL(url) }.getOrNull()
        if (parsed?.protocol != "https" || parsed.host.isBlank()) {
            showMessage("Invalid playback URL. An authorized HTTPS stream is required.")
            return
        }

        title = title.ifBlank { "Now playing" }
        val root = FrameLayout(this).apply {
            setBackgroundColor(0xFF101318.toInt())
        }
        val playerView = PlayerView(this)
        root.addView(playerView, FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        ))
        setContentView(root)

        player = ExoPlayer.Builder(this).build().also { exo ->
            playerView.player = exo
            exo.setMediaItem(MediaItem.fromUri(url))
            exo.addListener(object : Player.Listener {
                override fun onPlayerError(error: PlaybackException) {
                    showMessage("Video play nahi ho saka. Stream URL ya format check karein.")
                }
            })
            exo.prepare()
            exo.playWhenReady = true
        }
    }

    private fun showMessage(message: String) {
        setContentView(TextView(this).apply {
            text = message
            textSize = 16f
            setTextColor(0xFFFFFFFF.toInt())
            setPadding(24, 24, 24, 24)
            setBackgroundColor(0xFF101318.toInt())
        })
    }

    override fun onStop() {
        player?.release()
        player = null
        super.onStop()
    }

    companion object {
        const val EXTRA_URL = "playback_url"
        const val EXTRA_TITLE = "playback_title"
    }
}