package de.openteilauto.openteilauto.ui.bookings

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import de.openteilauto.openteilauto.R
import java.text.SimpleDateFormat
import java.util.*

const val BOOKING_UID = "de.openteilauto.openteilauto.BookingExtra"

class BookingsDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bookings_detail)

        var bookingUID: String? = null

        val bundle = intent.extras
        if (bundle != null) {
            bookingUID = bundle.getString(BOOKING_UID)
        }

        val bookingTitle = findViewById<TextView>(R.id.booking_title)
        val beginText = findViewById<TextView>(R.id.booking_begin_text)
        val endText = findViewById<TextView>(R.id.booking_end_text)
        val stationText = findViewById<TextView>(R.id.booking_station_text)

        val unlockButton = findViewById<Button>(R.id.button_unlock)
        val lockButton = findViewById<Button>(R.id.button_lock)

        val dateFormat = SimpleDateFormat("dd.MM.yyyy HH:mm")

        if (bookingUID != null) {
            val factory = BookingsDetailViewModelFactory(application, bookingUID)
            val model = ViewModelProvider(this, factory)[BookingsDetailViewModel::class.java]
            model.getBooking().observe(this, { booking ->
                bookingTitle.text = booking.vehicle.title
                beginText.text = dateFormat.format(booking.begin)
                endText.text = dateFormat.format(booking.end)
                stationText.text = booking.startingPoint.name

                if (booking.isCurrent(Date(System.currentTimeMillis()))) {
                    unlockButton.visibility = View.VISIBLE
                    lockButton.visibility = View.VISIBLE
                } else {
                    unlockButton.visibility = View.INVISIBLE
                    lockButton.visibility = View.INVISIBLE
                }
            })
        }
    }
}