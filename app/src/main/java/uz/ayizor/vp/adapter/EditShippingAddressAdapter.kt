package uz.ayizor.vp.adapter


import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import uz.ayizor.vp.databinding.ItemEditShippingAddressBinding
import uz.ayizor.vp.model.Location
import uz.ayizor.vp.utils.Utils


class EditShippingAddressAdapter(
    var context: Context,
    var arrayList: ArrayList<Location>,
    var onItemClickListener: OnItemClickListener
) : RecyclerView.Adapter<EditShippingAddressAdapter.EditShippingAddressViewHolder>() {
    private lateinit var binding: ItemEditShippingAddressBinding
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EditShippingAddressViewHolder {

        binding = ItemEditShippingAddressBinding.inflate(LayoutInflater.from(context), parent, false)
        return EditShippingAddressViewHolder(binding)


    }

    override fun onBindViewHolder(
        holder: EditShippingAddressViewHolder,
        position: Int
    ) {
        val location = arrayList[position]
        // Set text on radio button
        binding.tvTitle.text = location.location_name
        binding.tvFullAddress.text = location.location_latitude?.toDouble()?.let {
            location.location_longitude?.toDouble()?.let { it1 ->
                Utils.getCoordinateName(
                    context,
                    it,
                    it1
                )?.address.toString()
            }
        }


        // set listener on radio button
        binding.cvMain.setOnClickListener {
            onItemClickListener.onItemClickListener(location)
        }
    }


    override fun getItemCount(): Int {
        // pass total list size
        return arrayList.size
    }

    inner class EditShippingAddressViewHolder(val binding: ItemEditShippingAddressBinding) :
        RecyclerView.ViewHolder(binding.root)

    interface OnItemClickListener {
        fun onItemClickListener(location: Location)
    }
}