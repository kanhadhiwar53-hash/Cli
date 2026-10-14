package com.kanha.cli.data

import com.kanha.cli.BuildConfig
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

/**
 * Adapter for an HTTPS backend that the app owner controls and is authorized to use.
 * The uploaded ZIP is a Cloudstream extension, not a standalone Android SDK; its
 * private token/signature logic is not copied into this native app.
 *
 * GET {baseUrl}/search?q=QUERY
 * Response: {"results":[{"id":"...","title":"...","type":"movie|tv",
 * "overview":"...","posterUrl":"...","releaseDate":"..."}]}
 */
class MovieBoxBackendRepository(
    private val baseUrl: String = BuildConfig.MOVIEBOX_BACKEND_URL
) : MovieMetadataRepository {

    override fun search(query: String): MovieSearchResult {
        if (baseUrl.isBlank()) {
            return MovieSearchResult.Failure(
                "Backend configure nahi hai. README ka MovieBox backend setup section follow karein."
            )
        }
        if (!baseUrl.startsWith("https://")) {
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
                        posterUrl = item.optString("posterUrl")
                            .takeIf { it.isNotBlank() && it != "null" },
                        releaseDate = item.optString("releaseDate")
                            .takeIf { it.isNotBlank() && it != "null" }
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
}
