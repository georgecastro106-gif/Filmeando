package com.example.filmeando.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.filmeando.utils.FavoriteMovieEntity

@Dao
interface MovieDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(movie: FavoriteMovieEntity)

    @Delete
    suspend fun deleteFavorite(movie: FavoriteMovieEntity)

    @Query("SELECT * FROM favorite_movies")
    suspend fun getAllFavorites(): List<FavoriteMovieEntity>

    @Query("SELECT * FROM favorite_movies WHERE imdbID = :id")
    suspend fun getFavoriteById(id: String): FavoriteMovieEntity?
}