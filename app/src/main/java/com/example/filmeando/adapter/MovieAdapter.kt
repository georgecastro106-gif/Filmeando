package com.example.filmeando.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.filmeando.R
import com.example.filmeando.activities.MovieItem
import com.squareup.picasso.Picasso

class MovieAdapter(
    private var movieList: List<MovieItem>,
    private val onMovieClick: (String) -> Unit
) : RecyclerView.Adapter<MovieAdapter.MovieViewHolder>() {

    class MovieViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imgPoster: ImageView = view.findViewById(R.id.imgPoster)
        val txtTitle: TextView = view.findViewById(R.id.txtTitle)
        val txtYear: TextView = view.findViewById(R.id.txtOverview)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_movie, parent, false)
        return MovieViewHolder(view)
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        val movie = movieList[position]
        holder.txtTitle.text = movie.Title ?: "Sin título"
        holder.txtYear.text = "Año: ${movie.Year ?: "N/A"}"

        // Cargar imagen con Picasso
        // OMDb a veces devuelve "N/A" como póster, validamos que comience con http
        if (!movie.Poster.isNullOrEmpty() && movie.Poster != "N/A") {
            Picasso.get()
                .load(movie.Poster)
                .placeholder(android.R.drawable.ic_menu_gallery) // Imagen temporal mientras carga
                .error(android.R.drawable.ic_menu_report_image) // Imagen si falla
                .into(holder.imgPoster)
        } else {
            holder.imgPoster.setImageResource(android.R.drawable.ic_menu_report_image)
        }

        holder.itemView.setOnClickListener {
            movie.imdbID?.let { id ->
                onMovieClick(id)
            }
        }
    }

    override fun getItemCount(): Int = movieList.size

    fun updateData(newMovies: List<MovieItem>) {
        movieList = newMovies
        notifyDataSetChanged()
    }
}