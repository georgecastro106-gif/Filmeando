package com.example.filmeando.utils

import com.example.filmeando.activities.MovieDetail
import com.example.filmeando.activities.MovieSearchResponse
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface OmdbApiService {

    // 1. Llamada para buscar películas por nombre (parámetro 's')
    @GET("/")
    fun searchMovies(
        @Query("apikey") apiKey: String,
        @Query("s") query: String
    ): Call<MovieSearchResponse>

    // 2. Llamada para buscar el detalle de una película por su ID (parámetro 'i')
    @GET("/")
    fun getMovieDetails(
        @Query("apikey") apiKey: String,
        @Query("i") imdbId: String
    ): Call<MovieDetail>

    companion object {
        private const val BASE_URL = "https://www.omdbapi.com/"

        fun create(): OmdbApiService {
            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            return retrofit.create(OmdbApiService::class.java)
        }
    }
}