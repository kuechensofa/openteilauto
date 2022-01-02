package de.openteilauto.openteilauto.ui.search

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import de.openteilauto.openteilauto.R
import de.openteilauto.openteilauto.model.SearchResult
import de.openteilauto.openteilauto.ui.BaseActivity
import java.lang.NumberFormatException
import java.text.SimpleDateFormat

const val SEARCH_RESULT = "de.openteilauto.openteilauto.SearchResult"

class SearchResultDetailActivity : BaseActivity<SearchResultDetailViewModel>() {

    override var model: SearchResultDetailViewModel? = null

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_result_detail)

        val titleTextView: TextView = findViewById(R.id.result_title)
        val stationTextView: TextView = findViewById(R.id.result_station_text)
        val beginTextView: TextView = findViewById(R.id.result_begin_text)
        val endTextView: TextView = findViewById(R.id.result_end_text)

        val timeCostTextView: TextView = findViewById(R.id.time_cost_text)
        val kmCostTextView: TextView = findViewById(R.id.km_cost_text)
        val totalCostTextView: TextView = findViewById(R.id.total_cost_text)
        val kmEdit: EditText = findViewById(R.id.km_edit)
        var searchResult: SearchResult? = null

        kmEdit.setText("20")

        val dateFormat = SimpleDateFormat("dd.MM.yyyy HH:mm")

        model = ViewModelProvider(this)[SearchResultDetailViewModel::class.java]

        val bundle = intent.extras
        if (bundle != null) {
            searchResult = bundle.getParcelable(SEARCH_RESULT) as SearchResult?

            if (searchResult != null) {
                titleTextView.text = searchResult.vehicle.title
                stationTextView.text = searchResult.startingPoint.name
                beginTextView.text = dateFormat.format(searchResult.begin)
                endTextView.text = dateFormat.format(searchResult.end)
                timeCostTextView.text = formatPrice(searchResult.timeCost)
                model?.updatePriceEstimation(searchResult, kmEdit.text.toString().toInt())
            }
        }

        kmEdit.addTextChangedListener {
            if (it.toString() == "") {
                return@addTextChangedListener
            }

            if (searchResult != null) {
                try {
                    model?.updatePriceEstimation(searchResult, it.toString().toInt())
                } catch (e: NumberFormatException) {
                    Toast.makeText(this, R.string.enter_valid_number, Toast.LENGTH_SHORT).show()
                }
            }
        }

        model?.getTimePrice()?.observe(this, {price ->
            timeCostTextView.text = formatPrice(price)
        })

        model?.getKmPrice()?.observe(this, {price ->
            kmCostTextView.text = formatPrice(price)
        })

        model?.getTotalPrice()?.observe(this, {price ->
            totalCostTextView.text = formatPrice(price)
        })

        model?.getError()?.observe(this, {error ->
            Toast.makeText(this, error?.message, Toast.LENGTH_LONG).show()
        })
    }

    private fun formatPrice(price: Int): String {
        return "${price / 100},${(price % 100).toString().padStart(2, '0')} €"
    }
}