package de.openteilauto.openteilauto.ui.search

import android.Manifest
import android.content.Intent
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import de.openteilauto.openteilauto.R
import de.openteilauto.openteilauto.model.GeoPos
import de.openteilauto.openteilauto.ui.login.LoginActivity
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

private const val TAG = "SearchActivity"

class SearchActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        setTitle(R.string.search_title)

        val searchButton = findViewById<Button>(R.id.search_button)
        val beginDateEdit = findViewById<EditText>(R.id.begin_date_edit)
        val beginTimeEdit = findViewById<EditText>(R.id.begin_time_edit)
        val endDateEdit = findViewById<EditText>(R.id.end_date_edit)
        val endTimeEdit = findViewById<EditText>(R.id.end_time_edit)

        val model = ViewModelProvider(this)[SearchViewModel::class.java]

        val dateTimeFormat = SimpleDateFormat("HH:mm dd.MM.yyyy")

        var location: Location? = null
        val locationPermissionRequest = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            when {
                permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false -> {
                    Log.d(TAG, "Fine location access granted")
                    model.getLocation().observe(this, { newLocation ->
                        location = newLocation
                        model.requestGeocode(newLocation)
                    })
                }
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false -> {
                    Log.d(TAG, "Coarse location access granted")
                    model.getLocation().observe(this, { newLocation ->
                        location = newLocation
                        model.requestGeocode(newLocation)
                    })
                }
                else -> {
                    Toast.makeText(this, R.string.location_required, Toast.LENGTH_LONG).show()
                    Log.d(TAG, "No location permission granted")
                }
            }
        }

        val addressTextView = findViewById<TextView>(R.id.address_text_view)
        model.getGeocode().observe(this, { address ->
            addressTextView.text = address.getAddressLine(0)
        })

        locationPermissionRequest.launch(arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION))

        searchButton.setOnClickListener {
            // check date and time input formats
            if (!isDateInputValid(beginDateEdit)
                || !isDateInputValid(endDateEdit)
                || !isTimeInputValid(beginTimeEdit)
                || !isTimeInputValid(endTimeEdit)) {
                return@setOnClickListener
            }

            var beginDateTime: Date? = null
            try {
                val beginDateTimeInput = "${beginTimeEdit.text} ${beginDateEdit.text}"
                beginDateTime = dateTimeFormat.parse(beginDateTimeInput)
            } catch (e: ParseException) {
                Toast.makeText(this, R.string.begin_date_time_invalid, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            var endDateTime: Date? = null
            try {
                val endDateTimeInput = "${endTimeEdit.text} ${endDateEdit.text}"
                endDateTime = dateTimeFormat.parse(endDateTimeInput)
            } catch (e: ParseException) {
                Toast.makeText(this, R.string.end_date_time_invalid, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (location != null) {
                val geoPos = GeoPos(location!!.longitude.toString(), location!!.latitude.toString())

                model.search(
                    "",
                    beginDateTime,
                    endDateTime,
                    listOf(),
                    geoPos
                )
            } else {
                Toast.makeText(this, R.string.location_required, Toast.LENGTH_LONG).show()
            }
        }

        model.getSearchResults().observe(this, { searchResults ->
            for (result in searchResults) {
                Log.d(null, result.vehicle.title)
            }
        })

        model.isNotLoggedIn().observe(this, {
            if (it) {
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
            }
        })

        model.getError().observe(this, { error ->
            if (error != null) {
                Toast.makeText(this, error.message, Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun isDateInputValid(dateEdit: EditText): Boolean {
        val dateRegex = Regex("""\d{2}\.\d{2}\.\d{4}""")
        val dateInput = dateEdit.text.toString()
        if (!dateRegex.matches(dateInput)) {
            dateEdit.error = getString(R.string.date_format_invalid)
            return false
        }

        dateEdit.error = null
        return true
    }

    private fun isTimeInputValid(timeEdit: EditText): Boolean {
        val timeRegex = Regex("""\d{2}:\d{2}""")
        val timeInput = timeEdit.text.toString()
        if (!timeRegex.matches(timeInput)) {
            timeEdit.error = getString(R.string.time_format_invalid)
            return false
        }

        timeEdit.error = null
        return true
    }
}