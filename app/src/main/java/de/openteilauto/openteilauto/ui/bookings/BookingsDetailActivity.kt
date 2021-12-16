package de.openteilauto.openteilauto.ui.bookings

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import de.openteilauto.openteilauto.R
import de.openteilauto.openteilauto.ui.login.LoginActivity
import java.text.SimpleDateFormat
import java.util.*

const val BOOKING_UID = "de.openteilauto.openteilauto.BookingExtra"

class BookingsDetailActivity : AppCompatActivity() {

    private var model: BookingsDetailViewModel? = null

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

        unlockButton.setOnClickListener {
            showPinDialog(it)
        }

        val dateFormat = SimpleDateFormat("dd.MM.yyyy HH:mm")

        if (bookingUID != null) {
            val factory = BookingsDetailViewModelFactory(application, bookingUID)
            model = ViewModelProvider(this, factory)[BookingsDetailViewModel::class.java]

            lockButton.setOnClickListener {
                model?.lockVehicle()
            }

            model?.getBooking()?.observe(this, { booking ->
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

            model?.isUnlockSuccessful()?.observe(this, { isSuccessful ->
                if (isSuccessful) {
                    Toast.makeText(this, "Successfully unlocked car", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Unlock wasn't successful", Toast.LENGTH_SHORT).show()
                }
            })

            model?.isLockSuccessful()?.observe(this, { isSuccessful ->
                if (isSuccessful) {
                    Toast.makeText(this, "Successfully locked car", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Lock wasn't successful", Toast.LENGTH_SHORT).show()
                }
            })

            model?.getError()?.observe(this, { error ->
                if (error != null) {
                    Toast.makeText(this, error.message, Toast.LENGTH_SHORT).show()
                }
            })

            model?.getNotLoggedIn()?.observe(this, { notLoggedIn ->
                if (notLoggedIn) {
                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                }
            })
        }
    }

    private fun showPinDialog(view: View) {
        val builder: AlertDialog.Builder = this.let {
            AlertDialog.Builder(it)
        }
        val dialogView = layoutInflater.inflate(R.layout.dialog_pin, null)
        builder.apply {
            setView(dialogView)
            setPositiveButton(R.string.ok,
                DialogInterface.OnClickListener { dialog, id ->
                    val pinEdit = dialogView.findViewById<EditText>(R.id.pin_edit)
                    model?.unlockVehicle(pinEdit.text.toString())
                })
            setNegativeButton(R.string.cancel,
                DialogInterface.OnClickListener { dialog, id ->

                })
        }
        builder.setMessage(R.string.enter_pin_message)
            .setTitle(R.string.enter_pin_title)

        val dialog = builder.create()
        dialog.show()
    }
}