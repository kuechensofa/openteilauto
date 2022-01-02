package de.openteilauto.openteilauto.ui.search

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.RecyclerView
import de.openteilauto.openteilauto.R
import de.openteilauto.openteilauto.model.SearchResult

const val SEARCH_RESULTS = "de.openteilauto.openteilauto.SearchResults"

class SearchResultsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_results)

        val searchResultsAdapter = SearchResultsAdapter { searchResult -> adapterOnClick(searchResult) }
        val searchResultsView: RecyclerView = findViewById(R.id.search_results_view)
        searchResultsView.adapter = searchResultsAdapter

        val searchResults: MutableList<SearchResult> = mutableListOf()
        val bundle = intent.extras
        if (bundle != null) {
            val parcelableArray = bundle.getParcelableArray(SEARCH_RESULTS)
            parcelableArray?.forEach { searchResults.add(it as SearchResult) }
        }

        searchResultsAdapter.submitList(searchResults.toList())
    }

    private fun adapterOnClick(searchResult: SearchResult) {
        val intent = Intent(this, SearchResultDetailActivity::class.java)
        intent.putExtra(SEARCH_RESULT, searchResult)
        startActivity(intent)
    }
}