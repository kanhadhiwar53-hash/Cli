package com.kanha.cli.data

import com.kanha.cli.BuildConfig
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

/**
 * Adapter for an HTTPS backend controlled by the app owner and authorized to use.
 * GET {baseUrl}/search?q=QUERY
 * Optional response fields: playbackUrl and embedUrl must be authorized URLs.
 * No private provider signing/token logic is embedded in the APK.
 */
class MovieBoxBackendRepository(
    private val baseUrl: String = BuildConfig.MOVIEBOX_BACKEND_URL
) : MovieMetadataRepository {

    override fun search(query: String): MovieSearchResult {
        if (baseUrl.isBlank()) {
            return MovieSearchResult.Failure(
                "Backend configure nahi hai. README ka backend setup section follow karein."
            )
        }
        val parsedBase = runCatching { URL(baseUrl) }.getOrNull()
        if (parsedBase?.protocol != "https") {
            return MovieSearchResult.Failure("Backend URL HTTPS hona chahiye.")
        }

        return try {
            val encodedQuery = URLEncoder.encode(query, Charsets.UTF_8.name())
            val connection = (URL(baseUrl.trimEnd('/') + "/search?q=" + encodedQuery)
                .openConnection() as HttpURLConnection).apply {
                requestMethod = "GET"
                connectTimeout = 10000
                readTimeout = 15000
                setRequestProperty("Accept", "application/json")
            }
            try {
                val code = connection.responseCode
                val stream = if (code in 200..299) connection.inputStream else connection.errorStream
                val body = stream?.bufferedReader(Charsets.UTF_8)?.use { it.readText() }.orEmpty()
                if (code !in 200..299) {
                    return MovieSearchResult.Failure("Backend request failed (HTTP $code).")
                }

                val root = JSONObject(body)
                val itemsArray = when {
                    root.opt("results") is JSONArray -> root.optJSONArray("results")
                    root.opt("items") is JSONArray -> root.optJSONArray("items")
                    else -> JSONArray()
                }
                val items = mutableListOf<MovieItem>()
                for (i in 0 until (itemsArray?.length() ?: 0)) {
                    val item = itemsArray?.optJSONObject(i) ?: continue
                    val id = item.optString("id").ifBlank { item.optString("subjectId") }
                    val title = item.optString("title").ifBlank { item.optString("name") }
                    if (id.isBlank() || title.isBlank()) continue
                    val type = item.optString("type", item.optString("mediaType", "movie"))
                    items += MovieItem(
                        id = id,
                        title = title,
                        mediaType = if (type.equals("tv", true) ||
                            type.equals("series", true) || type.equals("show", true)) {
                            MediaType.TV
                        } else MediaType.MOVIE,
                        overview = item.optString("overview"),
                        posterUrl = item.optString("posterUrl").safeHttpsOrNull(),
                        releaseDate = item.optString("releaseDate").takeIf { it.isNotBlank() && it != "null" },
                        playbackUrl = item.optString("playbackUrl").safeHttpsOrNull(),
                        embedUrl = item.optString("embedUrl").safeHttpsOrNull()
                    )
                }
                if (items.isEmpty()) MovieSearchResult.Failure("Koi result nahi mila.")
                else MovieSearchResult.Success(items)
            } finally {
                connection.disconnect()
            }
        } catch (_: Exception) {
            MovieSearchResult.Failure("Search nahi ho saka. Network ya backend response check karein.")
        }
    }

    private fun String.safeHttpsOrNull(): String? {
        if (isBlank() || this == "null") return null
        return runCatching { URL(this) }.getOrNull()
            ?.takeIf { it.protocol == "https" && it.host.isNotBlank() }?.toString()
    }
}