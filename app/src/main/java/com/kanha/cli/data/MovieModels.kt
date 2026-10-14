package com.kanha.cli.data

/** Normalized metadata and optional authorized playback sources. */
data class MovieItem(
    val id: String,
    val title: String,
    val mediaType: MediaType,
    val overview: String = "",
    val posterUrl: String? = null,
    val releaseDate: String? = null,
    val playbackUrl: String? = null,
    val embedUrl: String? = null
)

enum class MediaType { MOVIE, TV }

sealed interface MovieSearchResult {
    data class Success(val items: List<MovieItem>) : MovieSearchResult
    data class Failure(val message: String) : MovieSearchResult
}

/** Implement this against a provider/backend you are authorized to use. */
interface MovieMetadataRepository {
    fun search(query: String): MovieSearchResult
}