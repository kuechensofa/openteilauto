package de.openteilauto.openteilauto.ui.bookings

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import de.openteilauto.openteilauto.R
import de.openteilauto.openteilauto.model.Booking
import de.openteilauto.openteilauto.ui.login.LoginActivity

class BookingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bookings)

        val bookingsAdapter = BookingsAdapter { booking -> adapterOnClick(booking) }

        val bookingsView: RecyclerView = findViewById(R.id.bookings_view)
        bookingsView.adapter = bookingsAdapter

        val model = ViewModelProvider(this)[BookingsViewModel::class.java]
        model.getBookings().observe(this, { bookings ->
            bookingsAdapter.submitList(bookings)
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

    private fun adapterOnClick(booking: Booking) {
        Toast.makeText(this, booking.vehicle.title, Toast.LENGTH_SHORT).show()
        val intent = Intent(this, BookingsDetailActivity::class.java).apply {
            putExtra(BOOKING_UID, booking.uid)
        }
        startActivity(intent)
    }
}