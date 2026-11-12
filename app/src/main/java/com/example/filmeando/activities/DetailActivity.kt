package com.example.filmeando.activities

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.filmeando.R
import com.example.filmeando.data.AppDatabase
import com.example.filmeando.utils.FavoriteMovieEntity
import com.example.filmeando.utils.OmdbApiService
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetailActivity : AppCompatActivity() {

    private lateinit var imgPoster: ImageView
    private lateinit var txtTitle: TextView
    private lateinit var txtYear: TextView
    private lateinit var txtGenre: TextView
    private lateinit var txtDirector: TextView
    private lateinit var txtActors: TextView
    private lateinit var txtPlot: TextView
    private lateinit var txtRating: TextView

    private var isFavorite = false
    private var currentMovieDetail: MovieDetail? = null
    private lateinit var db: AppDatabase

    // Aquí colocas tu API Key directamente (o puedes usar ApiConstants.API_KEY si creaste el archivo de constantes)
    private val apiKey = "60523137"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Filmando"

        db = AppDatabase.getDatabase(this)

        imgPoster = findViewById(R.id.imgDetailPoster)
        txtTitle = findViewById(R.id.txtDetailTitle)
        txtYear = findViewById(R.id.txtDetailDate)
        txtGenre = findViewById(R.id.txtDetailRating)
        txtDirector = findViewById(R.id.txtDetailOverview)

        // Recibimos el ID enviado por el Intent desde la MainActivity
        val imdbId = intent.getStringExtra("EXTRA_IMDB_ID")

        if (imdbId != null) {
            checkIfFavorite(imdbId)
            fetchMovieDetails(imdbId)
        }
    }

    private fun checkIfFavorite(imdbId: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val fav = db.movieDao().getFavoriteById(imdbId)
            isFavorite = fav != null
            withContext(Dispatchers.Main) {
                invalidateOptionsMenu()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_detail, menu)
        val favItem = menu?.findItem(R.id.action_favorite)
        if (isFavorite) {
            favItem?.setIcon(android.R.drawable.star_on)
        } else {
            favItem?.setIcon(android.R.drawable.star_off)
        }
        return true
    }

    // GESTIÓN DE CLICS EN LA BARRA SUPERIOR
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish() // BOTÓN DE RETROCESO: Cierra la actividad actual y vuelve atrás
                return true
            }
            R.id.action_share -> {
                shareMovieInfo() // BOTÓN DE COMPARTIR
                return true
            }
            R.id.action_favorite -> {
                toggleFavorite() // BOTÓN DE FAVORITOS (Base de datos)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    // Acción para compartir la información de la película mediante Intent nativo de Android
    private fun shareMovieInfo() {
        currentMovieDetail?.let { movie ->
            val shareMessage = "¡Mira esta película! \n\nTítulo: ${movie.Title}\nAño: ${movie.Year}\nPaís: ${movie.Country}\n\nSinopsis:\n${movie.Plot}"
            val intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, shareMessage)
                type = "text/plain"
            }
            startActivity(Intent.createChooser(intent, "Compartir película usando"))
        } ?: Toast.makeText(this, "Espere a que carguen los detalles", Toast.LENGTH_SHORT).show()
    }

    private fun toggleFavorite() {
        currentMovieDetail?.let { movie ->
            val entity = FavoriteMovieEntity(
                imdbID = movie.imdbID ?: "",
                title = movie.Title,
                year = movie.Year,
                poster = movie.Poster,
                country = movie.Country,
                genre = movie.Genre,
                plot = movie.Plot
            )

            CoroutineScope(Dispatchers.IO).launch {
                if (isFavorite) {
                    db.movieDao().deleteFavorite(entity)
                    isFavorite = false
                } else {
                    db.movieDao().insertFavorite(entity)
                    isFavorite = true
                }
                withContext(Dispatchers.Main) {
                    invalidateOptionsMenu()
                    val msg = if (isFavorite) "Añadido a favoritos" else "Eliminado de favoritos"
                    Toast.makeText(this@DetailActivity, msg, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    // SEGUNDA LLAMADA: Obtener detalles por ID usando tu API Key y Picasso
    private fun fetchMovieDetails(imdbId: String) {
        OmdbApiService.create().getMovieDetails(apiKey, imdbId).enqueue(object : Callback<MovieDetail> {
            val context = this@DetailActivity

            override fun onResponse(call: Call<MovieDetail>, response: Response<MovieDetail>) {
                if (response.isSuccessful) {
                    val movie = response.body()
                    if (movie != null && movie.Response == "True") {
                        currentMovieDetail = movie

                        // Mostramos los datos detallados en la interfaz
                        txtTitle.text = movie.Title
                        txtYear.text = "Estreno: ${movie.Released} (${movie.Runtime})"
                        txtGenre.text = "País: ${movie.Country ?: "N/A"} | Género: ${movie.Genre} | ⭐ IMDb: ${movie.imdbRating}"
                        txtDirector.text = "Director: ${movie.Director}\n\nActores: ${movie.Actors}\n\nSinopsis:\n${movie.Plot}"

                        // Carga de imagen con Picasso validando que el póster sea válido
                        if (!movie.Poster.isNullOrEmpty() && movie.Poster != "N/A") {
                            Picasso.get()
                                .load(movie.Poster)
                                .placeholder(android.R.drawable.ic_menu_gallery)
                                .error(android.R.drawable.ic_menu_report_image)
                                .into(imgPoster)
                        } else {
                            imgPoster.setImageResource(android.R.drawable.ic_menu_report_image)
                        }
                    }
                }
            }

            override fun onFailure(call: Call<MovieDetail>, t: Throwable) {
                Toast.makeText(context, "Error al cargar detalles: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}