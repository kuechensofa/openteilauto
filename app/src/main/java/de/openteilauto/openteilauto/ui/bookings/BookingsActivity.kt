package de.openteilauto.openteilauto.ui.bookings

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import de.openteilauto.openteilauto.R
import de.openteilauto.openteilauto.ui.login.LoginActivity

class BookingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bookings)

        val model: BookingsViewModel by viewModels()
        model.getBookings().observe(this, { bookings ->
            val textView = findViewById<TextView>(R.id.textView)
            textView.text = bookings.toString()
        })

        model.isNotLoggedIn().observe(this, { notLoggedIn ->
            if (notLoggedIn) {
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
            }
        })

        model.getError().observe(this, { error ->
            if (error != null) {
                Toast.makeText(this, error.message, Toast.LENGTH_LONG).show()
            }
        })
    }
}