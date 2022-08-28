package com.ayizor.vodiypolymer.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import com.ayizor.vodiypolymer.R
import androidx.recyclerview.widget.RecyclerView
import com.ayizor.vodiypolymer.databinding.ItemProductBinding
import com.ayizor.vodiypolymer.manager.MainPrefManager
import com.ayizor.vodiypolymer.model.Product
import com.bumptech.glide.Glide


class ProductsAdapter(
    val context: Context,
    var postsList: ArrayList<Product>,
    private val onPostItemClickListener: OnPostItemClickListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    val TAG: String = ProductsAdapter::class.java.simpleName
    private lateinit var binding: ItemProductBinding
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        binding = ItemProductBinding.inflate(LayoutInflater.from(context), parent, false)
        return ItemMainPostViewHolder(binding)


    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
val currency= MainPrefManager(context).loadCurrency()
        with(holder) {
            with(postsList[position]) {

                //price
                binding.tvPrice.text = "$product_price So'm"


                //image



                Glide.with(context).load(product_image?.get(0)?.image_url).into(binding.ivImage)

                //rating
                binding.tvRating.text = product_rating.toString()
                //solds
                binding.tvSold.text = product_sold +" "+ context.getString(R.string.sold)
                //title
                binding.tvTitle.text = product_name
                //post click listener
                binding.llMain.setOnClickListener {
                    if (product_id != null) {
                        onPostItemClickListener.onPostItemClickListener(
                            product_id
                        )
                    }
                }


            }


        }

    }

    override fun getItemCount(): Int {
        return postsList.size
    }


    inner class ItemMainPostViewHolder(val binding: ItemProductBinding) :
        RecyclerView.ViewHolder(binding.root)

    interface OnPostItemClickListener {
        fun onPostItemClickListener(id: String)
    }

}


