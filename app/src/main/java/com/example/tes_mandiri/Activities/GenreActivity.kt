package com.example.tes_mandiri.Activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.tes_mandiri.R

class GenreActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_genre)

        val genreName = intent.getStringExtra("genre_name")
        supportActionBar?.title = genreName
    }

}

data class Movie(
    val id: Int,
    val original_title: String,
    val overview: String,
    val poster_path: String,
    val title: String
)

class MovieAdapter(private val movies: List<Movie>) : RecyclerView.Adapter<MovieAdapter.MovieViewHolder>() {

    class MovieViewHolder(val view: View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.movie_item, parent, false)
        return MovieViewHolder(view)
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        val movie = movies[position]
        // Set movie data to views in your layout
        // For example:
        // holder.view.findViewById<TextView>(R.id.title).text = movie.title
    }

    override fun getItemCount() = movies.size
}