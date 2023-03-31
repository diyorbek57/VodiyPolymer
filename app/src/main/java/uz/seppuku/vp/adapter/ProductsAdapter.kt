package uz.seppuku.vp.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import uz.seppuku.vp.R
import uz.seppuku.vp.databinding.ItemProductBinding
import uz.seppuku.vp.model.Product


class ProductsAdapter(
    val context: Context,
    private val onPostItemClickListener: OnPostItemClickListener
) : ListAdapter<Product, ProductsAdapter.ViewHolder>(DiffCallback()) {


    val TAG: String = "ProductsAdapter"
    private lateinit var binding: ItemProductBinding
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        binding = ItemProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)


    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = getItem(position)
        holder.bind(currentItem)
    }



    inner class ViewHolder(val binding: ItemProductBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(product: Product) {

            product.apply {

                //price
                binding.tvPrice.text = "$product_price So'm"


                //image


                Glide.with(context).load(product_image?.get(0)?.image_url).transition(
                    DrawableTransitionOptions.withCrossFade()).into(binding.ivImage)

                //rating
                binding.tvRating.text = product_rating.toString()
                //solds
                binding.tvSold.text = product_sold + " " + context.getString(R.string.sold)
                //title
                binding.tvTitle.text = product_name
                //post click listener
                binding.llMain.setOnClickListener {
                    if (product_id != null) {
                        onPostItemClickListener.onPostItemClickListener(
                            product_id, binding
                        )
                    }
                }
            }
        }
    }
    class DiffCallback : DiffUtil.ItemCallback<Product>() {
        override fun areItemsTheSame(oldItem: Product, newItem: Product) =
            oldItem.product_id == newItem.product_id

        override fun areContentsTheSame(oldItem: Product, newItem: Product) =
            oldItem == newItem

    }
    interface OnPostItemClickListener {
        fun onPostItemClickListener(id: String, binding: ItemProductBinding )
    }

}


