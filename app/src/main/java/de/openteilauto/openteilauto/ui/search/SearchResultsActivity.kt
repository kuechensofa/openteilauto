package de.openteilauto.openteilauto.ui.search

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.SearchView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import de.openteilauto.openteilauto.R
import de.openteilauto.openteilauto.model.GeoPos
import de.openteilauto.openteilauto.model.SearchResult
import de.openteilauto.openteilauto.model.VehicleClass
import de.openteilauto.openteilauto.ui.BaseActivity
import java.util.*

const val BEGIN_DATE_TIME = "de.openteilauto.openteilauto.BeginDateTime"
const val END_DATE_TIME = "de.openteilauto.openteilauto.EndDateTime"
const val VEHICLE_CLASSES = "de.openteilauto.openteilauto.VehicleClasses"
const val LATITUDE = "de.openteilauto.openteilauto.Latitude"
const val LONGITUDE = "de.openteilauto.openteilauto.Longitude"

class SearchResultsActivity : BaseActivity<SearchViewModel>() {

    override var model: SearchViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_results)

        model = ViewModelProvider(this)[SearchViewModel::class.java]

        val searchResultsAdapter = SearchResultsAdapter { searchResult -> adapterOnClick(searchResult) }
        val searchResultsView: RecyclerView = findViewById(R.id.search_results_view)
        searchResultsView.adapter = searchResultsAdapter

        val progressBar: ProgressBar = findViewById(R.id.search_progress_bar)

        val bundle = intent.extras
        if (bundle != null) {
            val beginDateTimeMillis = bundle.getLong(BEGIN_DATE_TIME)
            val endDateTimeMillis = bundle.getLong(END_DATE_TIME)
            val vehicleClassesArray = bundle.getParcelableArray(VEHICLE_CLASSES)
            val lat = bundle.getString(LATITUDE)
            val lon = bundle.getString(LONGITUDE)

            val vehicleClasses = vehicleClassesArray?.map { it as VehicleClass }

            if (beginDateTimeMillis != 0L && endDateTimeMillis != 0L && vehicleClasses != null
                && lat != null && lon != null) {
                val beginDateTime = Date(beginDateTimeMillis)
                val endDateTime = Date(endDateTimeMillis)

                model?.search(
                    "",
                    beginDateTime,
                    endDateTime,
                    vehicleClasses,
                    GeoPos(lon, lat)
                )
            } else {
                Toast
                    .makeText(this, R.string.invalid_search_parameters, Toast.LENGTH_LONG)
                    .show()
                val intent = Intent(this, SearchActivity::class.java)
                startActivity(intent)
            }
        }

        model?.getSearchResults()?.observe(this, {searchResults ->
            searchResultsAdapter.submitList(searchResults)
            progressBar.visibility = View.INVISIBLE
        })
    }

    private fun adapterOnClick(searchResult: SearchResult) {
        val intent = Intent(this, SearchResultDetailActivity::class.java)
        intent.putExtra(SEARCH_RESULT, searchResult)
        startActivity(intent)
    }
}