package com.example.tes_mandiri.Activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.compose.ui.semantics.Role.Companion.Button
import com.example.tes_mandiri.R


class IntroActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro)

        val buttonNavigate: Button = findViewById(R.id.button)

        // Set OnClickListener for the button
        buttonNavigate.setOnClickListener {
            // Create an Intent to navigate to the destination activity
            val intent = Intent(this, DashboardActivity::class.java)

            // Optionally, you can pass data to the destination activity using intent extras
            // intent.putExtra("key", value)

            // Start the destination activity
            startActivity(intent)
        }
    }
}