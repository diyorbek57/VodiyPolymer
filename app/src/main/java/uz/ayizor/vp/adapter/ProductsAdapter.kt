package uz.ayizor.vp.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import uz.ayizor.vp.R
import uz.ayizor.vp.databinding.ItemProductBinding
import uz.ayizor.vp.model.Product


class ProductsAdapter(
    val context: Context,
    private var postsList: ArrayList<Product>,
    private val onPostItemClickListener: OnPostItemClickListener
) : RecyclerView.Adapter<ProductsAdapter.ItemMainPostViewHolder>() {


    val TAG: String = ProductsAdapter::class.java.simpleName
    private lateinit var binding: ItemProductBinding
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemMainPostViewHolder {

        binding = ItemProductBinding.inflate(LayoutInflater.from(context), parent, false)
        return ItemMainPostViewHolder(binding)


    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ItemMainPostViewHolder, position: Int) {
        val product = postsList[position]
        holder.bindProduct(product)

    }

    override fun getItemCount(): Int {
        return postsList.size
    }


    inner class ItemMainPostViewHolder(val binding: ItemProductBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindProduct(product: Product) {

            with(product) {

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

    interface OnPostItemClickListener {
        fun onPostItemClickListener(id: String, binding: ItemProductBinding )
    }

}


