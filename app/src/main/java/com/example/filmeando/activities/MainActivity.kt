package com.example.filmeando.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.filmeando.R
import com.example.filmeando.adapter.MovieAdapter
import com.example.filmeando.utils.OmdbApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    private lateinit var etSearch: EditText
    private lateinit var btnSearch: Button
    private lateinit var recyclerView: RecyclerView
    private lateinit var movieAdapter: MovieAdapter

    private val apiKey = "60523137" // Reemplaza con tu clave de omdbapi.com

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

// 1. Configurar la Toolbar como barra principal
        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbarMain)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true) // Activa la flecha de retroceso (<)
        supportActionBar?.title = "Filmando - Buscar"


        etSearch = findViewById(R.id.etSearch)
        btnSearch = findViewById(R.id.btnSearch)
        recyclerView = findViewById(R.id.recyclerView)

        recyclerView.layoutManager = LinearLayoutManager(this)

        // Configuramos el adaptador pasando una función que captura el imdbID
        movieAdapter = MovieAdapter(emptyList()) { imdbId ->
            val intent = Intent(this, DetailActivity::class.java).apply {
                putExtra("EXTRA_IMDB_ID", imdbId)
            }
            startActivity(intent)
        }
        recyclerView.adapter = movieAdapter

        btnSearch.setOnClickListener {
            val query = etSearch.text.toString().trim()
            if (query.isNotEmpty()) {
                searchMoviesByName(query)
            } else {
                Toast.makeText(this, "Escribe el nombre de una película", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // PRIMERA LLAMADA: Búsqueda por nombre
    private fun searchMoviesByName(query: String) {
        OmdbApiService.create().searchMovies(apiKey, query)
            .enqueue(object : Callback<MovieSearchResponse> {
                val context = this@MainActivity

                override fun onResponse(
                    call: Call<MovieSearchResponse>,
                    response: Response<MovieSearchResponse>
                ) {
                    if (response.isSuccessful) {
                        val body = response.body()
                        if (body?.Response == "True") {
                            val movies = body.Search ?: emptyList()
                            movieAdapter.updateData(movies)
                        } else {
                            // OMDb avisa si la película no existe o la API Key es inválida dentro del JSON
                            Toast.makeText(
                                context,
                                "Aviso API: ${body?.Response}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        // Esto imprime en el Logcat si hay un error HTTP (ej. 401 Unauthorized por mala API Key)
                        Log.e(
                            "API_ERROR",
                            "Código: ${response.code()} - Mensaje: ${
                                response.errorBody()?.string()
                            }"
                        )
                        Toast.makeText(context, "Error en el servidor", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<MovieSearchResponse>, t: Throwable) {
                    // Esto se ejecuta si no hay internet o falla la URL base
                    Log.e("API_FAILURE", t.message.toString())
                    Toast.makeText(context, "Fallo de red: ${t.message}", Toast.LENGTH_LONG).show()
                }
            })
    }

    // 2. Programar la acción al presionar la flecha de retroceso
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            // Si quieres que al pulsar atrás regrese a la portada (SplashActivity):
            val intent = Intent(this, SplashActivity::class.java)
            startActivity(intent)
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}