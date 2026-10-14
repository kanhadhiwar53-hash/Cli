package com.kanha.cli.data

/**
 * Normalized metadata model for movies and TV shows.
 * Map your authorized metadata provider's JSON response into this model.
 */
data class MovieItem(
    val id: String,
    val title: String,
    val mediaType: MediaType,
    val overview: String = "",
    val posterUrl: String? = null,
    val releaseDate: String? = null
)

enum class MediaType {
    MOVIE,
    TV
}

sealed interface MovieSearchResult {
    data class Success(val items: List<MovieItem>) : MovieSearchResult
    data class Failure(val message: String) : MovieSearchResult
}

/**
 * Implement this interface against an API you are authorized to use.
 * Keep provider secrets on a backend instead of hard-coding them in the APK.
 */
interface MovieMetadataRepository {
    suspend fun search(query: String): MovieSearchResult
}
