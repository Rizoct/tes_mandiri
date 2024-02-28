package com.example.tes_mandiri.Activities

import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.example.tes_mandiri.Adapters.SliderAdapters
import com.example.tes_mandiri.Domian.SliderItems
import com.example.tes_mandiri.R
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import kotlin.concurrent.thread

data class Genre(val id: Int, val name: String)

class DashboardActivity : AppCompatActivity() {
    private var viewPager2: ViewPager2? = null
    private val slideHandler: Handler? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)
        initView()
        banners()
        thread {
            val url = URL("https://api.themoviedb.org/3/genre/movie/list?language=en")
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.setRequestProperty("Authorization", "Bearer eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiI3ZDYwOTVjNGI5Y2ViMTM3NTIxYzZkYjJjODE4ZmNhZiIsInN1YiI6IjY1ZGUxYjk5YTg5NGQ2MDE4NzBkOWMyMyIsInNjb3BlcyI6WyJhcGlfcmVhZCJdLCJ2ZXJzaW9uIjoxfQ.BBN_SwMExsgM4VwZ2-yL9tgkswEu3wEizhRVycAYZdI")
            connection.setRequestProperty("accept", "application/json")
            println("API Response: $url")
            val responseCode = connection.responseCode
            if (responseCode == 200) {
                println("API Response: 200")
                val reader = BufferedReader(InputStreamReader(connection.inputStream))
                val response = reader.readText()
                reader.close()

                val jsonArray = JSONObject(response).getJSONArray("genres")
                val genres = mutableListOf<Genre>()
                for (i in 0 until jsonArray.length()) {
                    val jsonObject = jsonArray.getJSONObject(i)
                    val genre = Genre(jsonObject.getInt("id"), jsonObject.getString("name"))
                    genres.add(genre)
                }
                println("genres= $genres")

                runOnUiThread {
                    val recyclerView = findViewById<RecyclerView>(R.id.view1)
                    recyclerView.layoutManager = GridLayoutManager(this, 3)
                    recyclerView.adapter = GenreAdapter(genres, this)
                    recyclerView.addItemDecoration(PaddingItemDecoration(10))

                    // Hide ProgressBar
                    val progressBar = findViewById<ProgressBar>(R.id.progressBar1)
                    progressBar.visibility = View.GONE
                }
            } else{
                println("API Response: $responseCode")
            }

            connection.disconnect()
        }
    }

    class PaddingItemDecoration(private val padding: Int) : RecyclerView.ItemDecoration() {
        override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
            outRect.left = padding
            outRect.right = padding
            outRect.bottom = padding

            // Add top margin only for the first item to avoid double space between items
            if (parent.getChildAdapterPosition(view) == 0) {
                outRect.top = padding
            }
        }
    }

    class GenreAdapter(private val genres: List<Genre>, private val context: Context) : RecyclerView.Adapter<GenreAdapter.GenreViewHolder>() {

        class GenreViewHolder(val textView: TextView) : RecyclerView.ViewHolder(textView)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GenreViewHolder {
            val textView = LayoutInflater.from(parent.context).inflate(R.layout.textview_genre, parent, false) as TextView
            return GenreViewHolder(textView)
        }

        override fun onBindViewHolder(holder: GenreViewHolder, position: Int) {
            val genre = genres[position]
            holder.textView.text = genre.name
            holder.textView.setOnClickListener {
                val intent = Intent(context, GenreActivity::class.java)
                intent.putExtra("genre_name", genre.name)
                context.startActivity(intent)
            }
        }

        override fun getItemCount() = genres.size
    }


    private fun banners() {
        val sliderItems: MutableList<SliderItems> = ArrayList()
        sliderItems.add(SliderItems(R.drawable.wide1))
        sliderItems.add(SliderItems(R.drawable.wide))
        sliderItems.add(SliderItems(R.drawable.wide3))
        viewPager2!!.adapter = SliderAdapters(sliderItems, viewPager2!!)
        viewPager2!!.clipToPadding = false
        viewPager2!!.offscreenPageLimit = 3
        viewPager2!!.getChildAt(0).overScrollMode = RecyclerView.OVER_SCROLL_ALWAYS
        val compositePageTransformer = CompositePageTransformer()
        compositePageTransformer.addTransformer(MarginPageTransformer(48))
        compositePageTransformer.addTransformer { page, position ->
            val r = 1 - Math.abs(position)
            page.scaleY = 0.85f + r * 0.15f
        }
        viewPager2!!.setPageTransformer(compositePageTransformer)
        viewPager2!!.currentItem = 1
        viewPager2!!.registerOnPageChangeCallback(object : OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                slideHandler?.removeCallbacks(sliderRunnable)
            }
        })
    }

    private val sliderRunnable =
        Runnable { viewPager2!!.currentItem = viewPager2!!.currentItem + 1 }

    override fun onResume() {
        super.onResume()
    }

    override fun onPause() {
        super.onPause()
    }

    private fun initView() {
        viewPager2 = findViewById(R.id.viewPagerSlider)
    }
}