package com.kanha.cli

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import android.webkit.SafeBrowsingResponse
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.TextView
import java.net.URL

/** Opens only an HTTPS embed URL from an authorized provider. */
class EmbedActivity : Activity() {
    private var allowedHost: String? = null

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val embedUrl = intent.getStringExtra(EXTRA_URL).orEmpty()
        val parsed = runCatching { URL(embedUrl) }.getOrNull()
        if (parsed?.protocol != "https" || parsed.host.isBlank()) {
            showMessage("Invalid embed URL. An authorized HTTPS embed is required.")
            return
        }
        allowedHost = parsed.host.lowercase()

        val webView = WebView(this)
        webView.settings.javaScriptEnabled = true
        webView.settings.domStorageEnabled = true
        webView.settings.allowFileAccess = false
        webView.settings.allowContentAccess = false
        webView.settings.javaScriptCanOpenWindowsAutomatically = false
        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
                val url = request.url
                // Keep top-level navigation on the configured embed host over HTTPS.
                return url.scheme != "https" || url.host?.lowercase() != allowedHost
            }
        }
        setContentView(webView)
        webView.loadUrl(embedUrl)
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

    companion object {
        const val EXTRA_URL = "embed_url"
    }
}