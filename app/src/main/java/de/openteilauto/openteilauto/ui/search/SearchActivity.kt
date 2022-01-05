package de.openteilauto.openteilauto.ui.search

import android.Manifest
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.chip.Chip
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import de.openteilauto.openteilauto.R
import de.openteilauto.openteilauto.model.GeoPos
import de.openteilauto.openteilauto.model.VehicleClass
import de.openteilauto.openteilauto.ui.BaseActivity
import java.text.SimpleDateFormat
import java.util.*

private const val TAG = "SearchActivity"

class SearchActivity : BaseActivity<SearchViewModel>() {

    override var model: SearchViewModel? = null

    private var beginDate: Calendar = Calendar.getInstance()
    private var endDate: Calendar = Calendar.getInstance()

    private var beginDateButton: Button? = null
    private var beginTimeButton: Button? = null
    private var endDateButton: Button? = null
    private var endTimeButton: Button? = null

    init {
        beginDate.time = Date(System.currentTimeMillis())
        endDate.time = Date(System.currentTimeMillis())
        endDate.add(Calendar.HOUR_OF_DAY, 1)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        setTitle(R.string.search_title)

        val searchButton = findViewById<Button>(R.id.search_button)

        beginDateButton = findViewById(R.id.begin_date_button)
        beginTimeButton = findViewById(R.id.begin_time_button)
        endDateButton = findViewById(R.id.end_date_button)
        endTimeButton = findViewById(R.id.end_time_button)

        refreshDateTimeButtonTexts()

        beginDateButton!!.setOnClickListener {
            val datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText(R.string.select_begin_date)
                .setSelection(beginDate.timeInMillis)
                .build()
            datePicker.show(supportFragmentManager, "tag")

            datePicker.addOnPositiveButtonClickListener {
                beginDate.time = Date(it)
                resetEndDate()
                refreshDateTimeButtonTexts()
            }
        }

        beginTimeButton!!.setOnClickListener {
            val timePicker = MaterialTimePicker.Builder()
                .setTitleText(R.string.select_begin_time)
                .setTimeFormat(TimeFormat.CLOCK_24H)
                .setHour(beginDate.get(Calendar.HOUR_OF_DAY))
                .setMinute(beginDate.get(Calendar.MINUTE))
                .build()
            timePicker.show(supportFragmentManager, "tag")

            timePicker.addOnPositiveButtonClickListener {
                beginDate.set(Calendar.HOUR_OF_DAY, timePicker.hour)
                beginDate.set(Calendar.MINUTE, timePicker.minute)
                resetEndDate()
                refreshDateTimeButtonTexts()
            }
        }

        endDateButton!!.setOnClickListener {
            val datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText(R.string.select_end_date)
                .setSelection(endDate.timeInMillis)
                .build()
            datePicker.show(supportFragmentManager, "tag")

            datePicker.addOnPositiveButtonClickListener {
                endDate.time = Date(it)
                refreshDateTimeButtonTexts()
            }
        }

        endTimeButton!!.setOnClickListener {
            val timePicker = MaterialTimePicker.Builder()
                .setTitleText(R.string.select_end_time)
                .setTimeFormat(TimeFormat.CLOCK_24H)
                .setHour(endDate.get(Calendar.HOUR_OF_DAY))
                .setMinute(endDate.get(Calendar.MINUTE))
                .build()
            timePicker.show(supportFragmentManager, "tag")

            timePicker.addOnPositiveButtonClickListener {
                endDate.set(Calendar.HOUR_OF_DAY, timePicker.hour)
                endDate.set(Calendar.MINUTE, timePicker.minute)
                refreshDateTimeButtonTexts()
            }
        }

        model = ViewModelProvider(this)[SearchViewModel::class.java]

        var location: GeoPos? = null
        model?.getLocation()?.observe(this, { newLocation ->
            location = newLocation
        })

        val addressTextView = findViewById<TextView>(R.id.address_text_view)
        model?.getGeocode()?.observe(this, { geocode ->
            addressTextView.text = geocode
        })

        val editLocationButton: Button = findViewById(R.id.button_edit_location)
        editLocationButton.setOnClickListener {
            editLocation()
        }

        val locationPermissionRequest = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            when {
                permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false -> {
                    Log.d(TAG, "Fine location access granted")
                    model?.updateLocation()
                }
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false -> {
                    Log.d(TAG, "Coarse location access granted")
                    model?.updateLocation()
                }
                else -> {
                    Toast.makeText(this, R.string.manual_location_warning, Toast.LENGTH_LONG).show()
                    Log.d(TAG, "No location permission granted")
                }
            }
        }

        locationPermissionRequest.launch(arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION))

        searchButton.setOnClickListener {

            if (location != null) {
                val intent = Intent(this, SearchResultsActivity::class.java)
                intent.putExtra(BEGIN_DATE_TIME, beginDate.time.time)
                intent.putExtra(END_DATE_TIME, endDate.time.time)
                intent.putExtra(VEHICLE_CLASSES, getSelectedVehicleClasses().toTypedArray())
                intent.putExtra(LATITUDE, location?.lat)
                intent.putExtra(LONGITUDE, location?.lon)
                startActivity(intent)
            } else {
                Toast.makeText(this, R.string.location_required, Toast.LENGTH_LONG).show()
            }
        }

        model?.getError()?.observe(this, { error ->
            if (error != null) {
                Toast.makeText(this, error.message, Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun getSelectedVehicleClasses(): List<VehicleClass> {
        val selectedVehicleClasses: MutableList<VehicleClass> = mutableListOf()

        val miniChip: Chip = findViewById(R.id.chip_mini)
        val smallChip: Chip = findViewById(R.id.chip_small)
        val deliveryVanChip: Chip = findViewById(R.id.chip_delivery_van)
        val compactChip: Chip = findViewById(R.id.chip_compact)
        val miniVanChip: Chip = findViewById(R.id.chip_mini_van)
        val mediumSizedChip: Chip = findViewById(R.id.chip_medium_sized)
        val transporterChip: Chip = findViewById(R.id.chip_transporter)
        val busChip: Chip = findViewById(R.id.chip_bus)

        if (miniChip.isChecked) {
            selectedVehicleClasses.add(VehicleClass.MINI)
        }

        if (smallChip.isChecked) {
            selectedVehicleClasses.add(VehicleClass.SMALL)
        }

        if (deliveryVanChip.isChecked) {
            selectedVehicleClasses.add(VehicleClass.DELIVERY_VAN)
        }

        if (compactChip.isChecked) {
            selectedVehicleClasses.add(VehicleClass.COMPACT)
        }

        if (miniVanChip.isChecked) {
            selectedVehicleClasses.add(VehicleClass.MINI_VAN)
        }

        if (mediumSizedChip.isChecked) {
            selectedVehicleClasses.add(VehicleClass.MEDIUM_SIZED)
        }

        if (transporterChip.isChecked) {
            selectedVehicleClasses.add(VehicleClass.TRANSPORTER)
        }

        if (busChip.isChecked) {
            selectedVehicleClasses.add(VehicleClass.BUS)
        }

        return selectedVehicleClasses.toList()
    }

    private fun editLocation() {
        // show address dialog
        val builder: AlertDialog.Builder = this.let {
            AlertDialog.Builder(it)
        }
        val dialogView = layoutInflater.inflate(R.layout.dialog_location, null)
        builder.apply {
            setView(dialogView)
            setPositiveButton(R.string.ok,
                DialogInterface.OnClickListener { dialog, id ->
                    val addressEdit: EditText = dialogView.findViewById(R.id.address_edit)
                    model?.locationByAddress(addressEdit.text.toString())
                })
            setNegativeButton(R.string.cancel,
                DialogInterface.OnClickListener { dialog, id ->

                })
        }
        builder.setMessage(R.string.edit_location_message)
            .setTitle(R.string.edit_location_title)

        val dialog = builder.create()
        dialog.show()
    }

    private fun refreshDateTimeButtonTexts() {
        val dateFormat = SimpleDateFormat(resources.getString(R.string.date_format))
        val timeFormat = SimpleDateFormat(resources.getString(R.string.time_format))

        beginDateButton!!.text = dateFormat.format(beginDate.time)
        beginTimeButton!!.text = timeFormat.format(beginDate.time)
        endDateButton!!.text = dateFormat.format(endDate.time)
        endTimeButton!!.text = timeFormat.format(endDate.time)
    }

    private fun resetEndDate() {
        if (endDate <= beginDate) {
            endDate.time = beginDate.time
            endDate.add(Calendar.HOUR_OF_DAY, 1)
        }
    }
}