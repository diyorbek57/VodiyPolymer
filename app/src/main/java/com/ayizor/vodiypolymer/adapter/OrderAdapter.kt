package com.ayizor.vodiypolymer.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ayizor.vodiypolymer.R
import com.ayizor.vodiypolymer.databinding.ItemOrderProductBinding
import com.ayizor.vodiypolymer.model.Order
import com.bumptech.glide.Glide


class OrderAdapter(
    private val context: Context,
    private var postsList: ArrayList<Order>,
    private val onActionButtonClickListener: OnActionButtonClickListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    val TAG: String = OrderAdapter::class.java.simpleName
    private lateinit var binding: ItemOrderProductBinding
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        binding = ItemOrderProductBinding.inflate(LayoutInflater.from(context), parent, false)
        return OrderViewHolder(binding)


    }

    @SuppressLint("SetTextI18n", "NotifyDataSetChanged")
    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        @SuppressLint("RecyclerView") position: Int
    ) {

        val product: Order = postsList[position]
        if (holder is OrderViewHolder) {
            with(holder) {

                binding.tvQuantity.text = "Quantity = " + product.product_total_quantity
                Glide.with(context).load(product.product_image?.get(0)?.image_url)
                    .placeholder(R.color.dark_gray).into(binding.ivImage)
                binding.tvTitle.text = product.product_name
                binding.tvPrice.text = product.product_total_price + " So'm"
            }
            binding.tvOrderAction.setOnClickListener {
                onActionButtonClickListener.onActionButtonClickListener(product)
            }
        }

    }


    override fun getItemCount(): Int {
        return postsList.size
    }


    inner class OrderViewHolder(val binding: ItemOrderProductBinding) :
        RecyclerView.ViewHolder(binding.root)

    interface OnActionButtonClickListener {
        fun onActionButtonClickListener(order: Order)
    }
}