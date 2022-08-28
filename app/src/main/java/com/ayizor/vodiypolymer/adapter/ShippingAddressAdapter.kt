package com.ayizor.vodiypolymer.adapter


import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ayizor.vodiypolymer.R
import com.ayizor.vodiypolymer.model.Location
import com.ayizor.vodiypolymer.utils.Utils


class ShippingAddressAdapter(
    var context: Context,
    var arrayList: ArrayList<Location>,
    var onRadioButtonClickListener: OnRadioButtonClickListener
) : RecyclerView.Adapter<ShippingAddressAdapter.ViewHolder>() {
    var selectedPosition = -1
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        // Initialize view
        val view: View = LayoutInflater.from(parent.context)
            .inflate(
                R.layout.item_shipping_address, parent,
                false
            )
        // Pass holder view
        return ViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        val location = arrayList[position]
        // Set text on radio button
        holder.title.text = location.location_name
        holder.locationDetails.text = location.location_latitude?.toDouble()?.let {
            location.location_longitude?.toDouble()?.let { it1 ->
                Utils.getCoordinateName(
                    context,
                    it,
                    it1
                )?.address.toString()
            }
        }

        // Checked selected radio button
        holder.radioButton.isChecked = (position == selectedPosition)

        // set listener on radio button
        holder.radioButton.setOnCheckedChangeListener { compoundButton, b ->
            // check condition
            if (b) {
                // When checked
                // update selected position
                selectedPosition = position
                // Call listener

                location.location_id?.let { onRadioButtonClickListener.onRadioButtonClickListener(it) }

            }
        }
    }


    override fun getItemCount(): Int {
        // pass total list size
        return arrayList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // Initialize variable
        var radioButton: RadioButton
        var title: TextView
        var locationDetails: TextView

        init {

            // Assign variable
            radioButton = itemView.findViewById(R.id.radio_button)
            title = itemView.findViewById(R.id.tv_title)
            locationDetails = itemView.findViewById(R.id.tv_full_address)
        }
    }

    interface OnRadioButtonClickListener {
        fun onRadioButtonClickListener(id: String)
    }
}