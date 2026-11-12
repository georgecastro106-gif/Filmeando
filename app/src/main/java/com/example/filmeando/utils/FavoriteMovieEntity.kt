package com.example.filmeando.utils

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_movies")
data class FavoriteMovieEntity(
    @PrimaryKey val imdbID: String,
    val title: String?,
    val year: String?,
    val poster: String?,
    val country: String?,
    val genre: String?,
    val plot: String?
)
