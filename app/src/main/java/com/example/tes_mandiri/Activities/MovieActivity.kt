package com.example.tes_mandiri.Activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.tes_mandiri.R
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import kotlin.concurrent.thread


class MovieActivity : AppCompatActivity() {
    data class MovieData(
        val id: Int,
        val original_title: String,
        val overview: String,
        val poster_path: String,
        val rating: Double,
        val rating_count: Int,
    )

    data class Review(
        val author: String,
        val content: String,
        val url: String,
        val rating: Int,
    )


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie)

        val movie_title = intent.getStringExtra("movie_title")
        val movie_id = intent.getStringExtra("movie_id")
        supportActionBar?.title = movie_title


        thread {
            val url = URL("https://api.themoviedb.org/3/movie/${movie_id}?language=en-US")
            println(url);
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

                val jsonObject = JSONObject(response)
                val movie = MovieData(
                    jsonObject.getInt("id"),
                    jsonObject.getString("original_title"),
                    jsonObject.getString("overview"),
                    jsonObject.getString("poster_path"),
                    jsonObject.getDouble("vote_average"),
                    jsonObject.getInt("vote_count"),

                    )

                runOnUiThread {
                    val titleText = findViewById<TextView>(R.id.overviewTextView)
                    val imageView = findViewById<ImageView>(R.id.movie_poster)
                    val ratingBar = findViewById<RatingBar>(R.id.ratingBar)
                    val voteCountTextView = findViewById<TextView>(R.id.voteCountTextView)
                    titleText.text = movie.overview

                    Glide.with(this)
                        .load("https://image.tmdb.org/t/p/w500/${movie.poster_path}")
                        .into(imageView)
                    ratingBar.rating = movie.rating.toFloat()
                    voteCountTextView.text = "(${movie.rating_count})"
                }

                val url = URL("https://api.themoviedb.org/3/movie/${movie_id}/reviews?language=en-US&page=1")
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                connection.setRequestProperty("Authorization", "Bearer eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiI3ZDYwOTVjNGI5Y2ViMTM3NTIxYzZkYjJjODE4ZmNhZiIsInN1YiI6IjY1ZGUxYjk5YTg5NGQ2MDE4NzBkOWMyMyIsInNjb3BlcyI6WyJhcGlfcmVhZCJdLCJ2ZXJzaW9uIjoxfQ.BBN_SwMExsgM4VwZ2-yL9tgkswEu3wEizhRVycAYZdI")
                connection.setRequestProperty("accept", "application/json")

                val responseCode = connection.responseCode
                if (responseCode == 200) {
                    val reader = BufferedReader(InputStreamReader(connection.inputStream))
                    val response = reader.readText()
                    reader.close()

                    val jsonObject = JSONObject(response)
                    val jsonArray = jsonObject.getJSONArray("results")
                    val reviews = mutableListOf<Review>()
                    for (i in 0 until jsonArray.length()) {
                        val reviewObject = jsonArray.getJSONObject(i)
                        val authorDetailsObject = reviewObject.getJSONObject("author_details")
                        val review = Review(
                            reviewObject.getString("author"),
                            reviewObject.getString("content"),
                            reviewObject.getString("url"),
                            authorDetailsObject.getInt("rating")
                        )
                        reviews.add(review)
                    }


                    runOnUiThread {
                        val reviewsLayout = findViewById<LinearLayout>(R.id.reviewsLayout)
                        for (review in reviews) {
                            val reviewTextView = TextView(this)
                            reviewTextView.text = "${review.author} | ${review.rating}\n----------------------------------\n${review.content}"
                            reviewsLayout.addView(reviewTextView)
                        }
                    }
                }
            }
            connection.disconnect()
        }

    }
}