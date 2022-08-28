package com.ayizor.vodiypolymer.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.ayizor.vodiypolymer.R
import com.ayizor.vodiypolymer.model.Order
import com.bumptech.glide.Glide
import com.google.firebase.database.*


class CheckoutAdapter(
    val context: Context,
    var postsList: ArrayList<Order>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    val TAG: String = CheckoutAdapter::class.java.simpleName
    val ref = FirebaseDatabase.getInstance().reference
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_checkout_product, parent, false)
        return CartViewHolder(view)


    }

    @SuppressLint("SetTextI18n", "NotifyDataSetChanged")
    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        @SuppressLint("RecyclerView") position: Int
    ) {

        val product: Order = postsList[position]
        if (holder is CartViewHolder) {

            holder.quantity.text = product.product_total_quantity
            Glide.with(context).load(product.product_image?.get(0)?.image_url)
                .placeholder(R.color.dark_gray).into(holder.image)
            holder.description.text = product.product_description
            holder.title.text = product.product_name
            holder.total_price.text = product.product_total_price+" So'm"
        }


    }



    override fun getItemCount(): Int {
        return postsList.size
    }


    class CartViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var image: ImageView
        var mainRl: CardView
        var title: TextView
        var description: TextView
        var total_price: TextView
        var quantity: TextView


        init {
            image = itemView.findViewById(R.id.iv_image)
            mainRl = itemView.findViewById(R.id.ll_main)
            title = itemView.findViewById(R.id.tv_title)
            description = itemView.findViewById(R.id.tv_description)
            total_price = itemView.findViewById(R.id.tv_price)
            quantity = itemView.findViewById(R.id.tv_quantity)
        }
    }


}