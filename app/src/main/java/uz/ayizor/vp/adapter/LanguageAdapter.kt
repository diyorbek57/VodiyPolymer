package uz.ayizor.vp.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import uz.ayizor.vp.R
import uz.ayizor.vp.model.Language

class LanguageAdapter(
    var context: Context,
    var arrayList: ArrayList<Language>,
    var onRadioButtonClickListener: OnRadioButtonClickListener
) : RecyclerView.Adapter<LanguageAdapter.ViewHolder>() {
    var selectedPosition = -1
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        // Initialize view
        val view: View = LayoutInflater.from(parent.context)
            .inflate(
                R.layout.item_choose_language, parent,
                false
            )
        // Pass holder view
        return ViewHolder(view)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        val language = arrayList[position]
        // Set text on radio button
        holder.title.text = language.language_name
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

                language.language_code?.let {
                    onRadioButtonClickListener.onRadioButtonClickListener(
                        it
                    )
                }

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


        init {

            // Assign variable
            radioButton = itemView.findViewById(R.id.radio_button)
            title = itemView.findViewById(R.id.tv_title)
        }
    }

    interface OnRadioButtonClickListener {
        fun onRadioButtonClickListener(code: String)
    }
}