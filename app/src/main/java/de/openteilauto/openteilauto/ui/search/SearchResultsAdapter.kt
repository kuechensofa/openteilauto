package de.openteilauto.openteilauto.ui.search

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import de.openteilauto.openteilauto.R
import de.openteilauto.openteilauto.model.SearchResult

class SearchResultsAdapter(private val onClick: (SearchResult) -> Unit) :
    ListAdapter<SearchResult, SearchResultsAdapter.SearchResultsViewHolder>(SearchResultDiffCallback) {

    class SearchResultsViewHolder(itemView: View, onClick: (SearchResult) -> Unit) :
        RecyclerView.ViewHolder(itemView) {
        private val resultTextView: TextView = itemView.findViewById(R.id.result_text)
        private val resultStationTextView: TextView = itemView.findViewById(R.id.result_station_text)
        private var currentResult: SearchResult? = null

        init {
            itemView.setOnClickListener {
                currentResult?.let {
                    onClick(it)
                }
            }
        }

        fun bind(searchResult: SearchResult) {
            currentResult = searchResult

            resultTextView.text = searchResult.vehicle.title
            resultStationTextView.text = searchResult.startingPoint.name
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchResultsViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.search_result_item, parent, false)
        return SearchResultsViewHolder(view, onClick)
    }

    override fun onBindViewHolder(holder: SearchResultsViewHolder, position: Int) {
        val searchResult = getItem(position)
        holder.bind(searchResult)
    }
}

object SearchResultDiffCallback : DiffUtil.ItemCallback<SearchResult>() {
    override fun areItemsTheSame(oldItem: SearchResult, newItem: SearchResult): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: SearchResult, newItem: SearchResult): Boolean {
        return oldItem.uid == newItem.uid
    }
}