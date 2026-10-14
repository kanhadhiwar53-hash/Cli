package com.kanha.cli

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import com.kanha.cli.data.MediaType
import com.kanha.cli.data.MovieBoxBackendRepository
import com.kanha.cli.data.MovieItem
import com.kanha.cli.data.MovieSearchResult
import java.util.concurrent.Executors

class MainActivity : Activity() {
    private val executor = Executors.newSingleThreadExecutor()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val searchInput = findViewById<EditText>(R.id.searchInput)
        val searchButton = findViewById<Button>(R.id.searchButton)
        val statusText = findViewById<TextView>(R.id.statusText)
        val loadingProgress = findViewById<ProgressBar>(R.id.loadingProgress)
        val resultsContainer = findViewById<LinearLayout>(R.id.resultsContainer)
        val repository = MovieBoxBackendRepository()

        fun renderResults(items: List<MovieItem>) {
            resultsContainer.removeAllViews()
            items.forEach { item ->
                val kind = if (item.mediaType == MediaType.TV) "SERIES" else "MOVIE"
                val card = TextView(this).apply {
                    text = buildString {
                        append(item.title)
                        append("  •  ")
                        append(kind)
                        item.releaseDate?.let { append("\n"); append(it) }
                        if (item.overview.isNotBlank()) {
                            append("\n\n")
                            append(item.overview)
                        }
                    }
                    textSize = 15f
                    setTextColor(0xFFE8ECF4.toInt())
                    setPadding(16, 16, 16, 16)
                    setBackgroundColor(0xFF1B2029.toInt())
                }
                val params = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply { bottomMargin = dp(10) }
                resultsContainer.addView(card, params)
            }
        }

        fun search() {
            val query = searchInput.text.toString().trim()
            if (query.isEmpty()) {
                searchInput.error = "Movie ya series ka naam likhein"
                return
            }
            (getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager)
                ?.hideSoftInputFromWindow(searchInput.windowToken, 0)

            searchButton.isEnabled = false
            loadingProgress.visibility = View.VISIBLE
            statusText.text = "Searching..."
            resultsContainer.removeAllViews()

            executor.execute {
                val result = repository.search(query)
                runOnUiThread {
                    loadingProgress.visibility = View.GONE
                    searchButton.isEnabled = true
                    when (result) {
                        is MovieSearchResult.Success -> {
                            statusText.text = "${result.items.size} result(s) mile."
                            renderResults(result.items)
                        }
                        is MovieSearchResult.Failure -> statusText.text = result.message
                    }
                }
            }
        }

        searchButton.setOnClickListener { search() }
        searchInput.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                search()
                true
            } else false
        }

        statusText.text = if (BuildConfig.MOVIEBOX_BACKEND_URL.isBlank()) {
            "ZIP provider review ho gaya. Search chalane ke liye apna authorized HTTPS backend configure karein."
        } else {
            "Movie ya series search karein."
        }
    }

    private fun dp(value: Int): Int = (value * resources.displayMetrics.density).toInt()

    override fun onDestroy() {
        executor.shutdownNow()
        super.onDestroy()
    }
}
