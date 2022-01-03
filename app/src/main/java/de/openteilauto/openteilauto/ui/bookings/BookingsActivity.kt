package de.openteilauto.openteilauto.ui.bookings

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import de.openteilauto.openteilauto.R
import de.openteilauto.openteilauto.model.Booking
import de.openteilauto.openteilauto.ui.BaseActivity
import de.openteilauto.openteilauto.ui.login.LoginActivity
import de.openteilauto.openteilauto.ui.search.SearchActivity

class BookingsActivity : BaseActivity<BookingsViewModel>() {
    override var model: BookingsViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bookings)

        setTitle(R.string.bookings_list_title)

        model = ViewModelProvider(this)[BookingsViewModel::class.java]

        val bookingsAdapter = BookingsAdapter { booking -> adapterOnClick(booking) }

        val bookingsView: RecyclerView = findViewById(R.id.bookings_view)
        bookingsView.adapter = bookingsAdapter

        val swipeRefreshLayout = findViewById<SwipeRefreshLayout>(R.id.bookings_swipe_refresh)

        val searchActionButton = findViewById<FloatingActionButton>(R.id.search_action_button)
        searchActionButton.setOnClickListener {
            startSearchActivity()
        }

        val progressBar: ProgressBar = findViewById(R.id.bookings_progress_bar)

        model?.getBookings()?.observe(this, { bookings ->
            swipeRefreshLayout.isRefreshing = false
            progressBar.visibility = View.INVISIBLE
            bookingsAdapter.submitList(bookings)
        })

        model?.isNotLoggedIn()?.observe(this, { notLoggedIn ->
            if (notLoggedIn) {
                swipeRefreshLayout.isRefreshing = false
                progressBar.visibility = View.INVISIBLE
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
            }
        })

        model?.getError()?.observe(this, { error ->
            if (error != null) {
                swipeRefreshLayout.isRefreshing = false
                progressBar.visibility = View.INVISIBLE
                Toast.makeText(this, error.message, Toast.LENGTH_LONG).show()
            }
        })

        swipeRefreshLayout.setOnRefreshListener {
            model?.refreshBookings()
        }
    }

    private fun adapterOnClick(booking: Booking) {
        val intent = Intent(this, BookingsDetailActivity::class.java).apply {
            putExtra(BOOKING_UID, booking.uid)
        }
        startActivity(intent)
    }
}