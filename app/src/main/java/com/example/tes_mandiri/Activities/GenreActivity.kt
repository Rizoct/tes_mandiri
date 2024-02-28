package com.example.tes_mandiri.Activities

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import com.bumptech.glide.request.target.Target
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.example.tes_mandiri.R
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import kotlin.concurrent.thread

class GenreActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_genre)

        val genreName = intent.getStringExtra("genre_name")
        supportActionBar?.title = genreName

        val genreId = intent.getIntExtra("genre_id", 0)


        thread {
            val url = URL("https://api.themoviedb.org/3/discover/movie?include_adult=false&include_video=true&language=en-US&page=1&sort_by=popularity.desc&with_genres=$genreId")
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.setRequestProperty("Authorization", "Bearer eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiI3ZDYwOTVjNGI5Y2ViMTM3NTIxYzZkYjJjODE4ZmNhZiIsInN1YiI6IjY1ZGUxYjk5YTg5NGQ2MDE4NzBkOWMyMyIsInNjb3BlcyI6WyJhcGlfcmVhZCJdLCJ2ZXJzaW9uIjoxfQ.BBN_SwMExsgM4VwZ2-yL9tgkswEu3wEizhRVycAYZdI")
            connection.setRequestProperty("accept", "application/json")

            val responseCode = connection.responseCode
            println("response baru = $responseCode")
            if (responseCode == 200) {
                val reader = BufferedReader(InputStreamReader(connection.inputStream))
                val response = reader.readText()
                reader.close()

                val jsonArray = JSONObject(response).getJSONArray("results")
                val movies = mutableListOf<Movie>()
                for (i in 0 until jsonArray.length()) {
                    val jsonObject = jsonArray.getJSONObject(i)
                    val movie = Movie(
                        jsonObject.getInt("id"),
                        jsonObject.getString("original_title"),
                        jsonObject.getString("overview"),
                        jsonObject.getString("poster_path"),
                        jsonObject.getString("title")
                    )
                    movies.add(movie)
                }

                runOnUiThread {
                    val recyclerView = findViewById<RecyclerView>(R.id.view1)
                    recyclerView.layoutManager = GridLayoutManager(this, 3)
                    recyclerView.adapter = MovieAdapter(movies, this)

                    // Hide ProgressBar
                    val progressBar = findViewById<ProgressBar>(R.id.progressBar1)
                    progressBar.visibility = View.GONE
                }
            }

            connection.disconnect()
        }
    }
}


data class Movie(
    val id: Int,
    val original_title: String,
    val overview: String,
    val poster_path: String,
    val title: String
)

class MovieAdapter(private val movies: List<Movie>, private val context: Context) : RecyclerView.Adapter<MovieAdapter.MovieViewHolder>() {

    class MovieViewHolder(val view: View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.movie_item, parent, false)
        return MovieViewHolder(view)
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        val movie = movies[position]
        holder.view.findViewById<TextView>(R.id.movie_title).text = movie.title

        val imageView = holder.view.findViewById<ImageView>(R.id.movie_poster)
        val progressBar = holder.view.findViewById<ProgressBar>(R.id.progress_bar)

        progressBar.visibility = View.VISIBLE

        Glide.with(holder.view.context)
            .load("https://image.tmdb.org/t/p/w500/${movie.poster_path}")
            .apply(RequestOptions().override(Target.SIZE_ORIGINAL))
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                    progressBar.visibility = View.GONE
                    return false
                }

                override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                    progressBar.visibility = View.GONE
                    return false
                }
            })
            .into(imageView)
    }

    override fun getItemCount() = movies.size
}


