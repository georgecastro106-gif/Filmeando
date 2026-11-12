package com.example.filmeando.activities

import java.io.Serializable

// Modelo para la respuesta de búsqueda por nombre
data class MovieSearchResponse(
    val Search: List<MovieItem>?,
    val totalResults: String?,
    val Response: String?
) : Serializable

data class MovieItem(
    val Title: String?,
    val Year: String?,
    val imdbID: String?,
    val Type: String?,
    val Poster: String?
) : Serializable

// Modelo para la respuesta detallada por ID
data class MovieDetail(
    val Title: String?,
    val Year: String?,
    val Rated: String?,
    val Released: String?,
    val Runtime: String?,
    val Genre: String?,
    val Director: String?,
    val Actors: String?,
    val Plot: String?,
    val Poster: String?,
    val imdbRating: String?,
    val imdbID: String?,
    val Response: String?,
    val Country: String?
) : Serializable