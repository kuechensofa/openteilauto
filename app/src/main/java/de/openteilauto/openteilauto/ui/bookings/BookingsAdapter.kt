package de.openteilauto.openteilauto.ui.bookings

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import de.openteilauto.openteilauto.R
import de.openteilauto.openteilauto.model.Booking
import java.text.SimpleDateFormat

class BookingsAdapter(private val onClick: (Booking) -> Unit) :
    ListAdapter<Booking, BookingsAdapter.BookingViewHolder>(BookingDiffCallback) {

    class BookingViewHolder(itemView: View, onClick: (Booking) -> Unit) :
        RecyclerView.ViewHolder(itemView) {
        private val bookingTextView: TextView = itemView.findViewById(R.id.booking_text)
        private val bookingTimeTextView: TextView = itemView.findViewById(R.id.booking_time_text)
        private val bookingStationTextView: TextView = itemView.findViewById(R.id.booking_station_text)
        private var currentBooking: Booking? = null

        init {
            itemView.setOnClickListener {
                currentBooking?.let {
                    onClick(it)
                }
            }
        }

        fun bind(booking: Booking) {
            currentBooking = booking

            bookingTextView.text = booking.vehicle.title
            val dateFormat = SimpleDateFormat("dd.MM.yyyy HH:mm")
            bookingTimeTextView.text =
                itemView.resources.getString(R.string.booking_time,
                    dateFormat.format(booking.begin), dateFormat.format(booking.end))

            bookingStationTextView.text = booking.startingPoint.name
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookingViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.booking_item, parent, false)
        return BookingViewHolder(view, onClick)
    }

    override fun onBindViewHolder(holder: BookingViewHolder, position: Int) {
        val booking = getItem(position)
        holder.bind(booking)
    }
}

object BookingDiffCallback : DiffUtil.ItemCallback<Booking>() {
    override fun areItemsTheSame(oldItem: Booking, newItem: Booking): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: Booking, newItem: Booking): Boolean {
        return oldItem.uid == newItem.uid
    }
}