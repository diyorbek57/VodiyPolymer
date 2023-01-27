package uz.seppuku.vp.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.database.*
import uz.seppuku.vp.databinding.ItemCheckoutProductBinding
import uz.seppuku.vp.model.Cart
import uz.seppuku.vp.model.Product
import uz.seppuku.vp.R

class CheckoutAdapter(
    val context: Context,
    var postsList: ArrayList<Cart>
) : RecyclerView.Adapter<CheckoutAdapter.CheckoutViewHolder>() {


    val TAG: String = CheckoutAdapter::class.java.simpleName
    val ref = FirebaseDatabase.getInstance().reference
    private lateinit var binding: ItemCheckoutProductBinding
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CheckoutViewHolder {

        binding = ItemCheckoutProductBinding.inflate(LayoutInflater.from(context), parent, false)
        return CheckoutViewHolder(binding)


    }

    @SuppressLint("SetTextI18n", "NotifyDataSetChanged")
    override fun onBindViewHolder(
        holder: CheckoutViewHolder,
        @SuppressLint("RecyclerView") position: Int
    ) {

        val cart: Cart = postsList[position]
        holder.getProduct(cart, cart.cart_product_id.toString(), holder)

    }


    override fun getItemCount(): Int {
        return postsList.size
    }


    inner class CheckoutViewHolder(val binding: ItemCheckoutProductBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun getProduct(cart: Cart, product_id: String, holder: CheckoutAdapter.CheckoutViewHolder) {
            var product: Product? = null
            val productListener = object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    for (postSnapshot in dataSnapshot.children) {

                        product = postSnapshot.getValue(Product::class.java)

                    }
                    if (product != null) {
                        holder.bindCheckout(cart, product!!)
                    }


                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Getting Post failed, log a message
                    Log.w(TAG, "loadPost: onCancelled", databaseError.toException())
                }
            }
            ref.child("products").orderByChild("product_id").equalTo(product_id)
                .addValueEventListener(productListener)
        }

        @SuppressLint("SetTextI18n")
        fun bindCheckout(cart: Cart, product: Product) {
            with(product) {
                with(cart) {


                    binding.tvQuantity.text = cart_product_total_quantity
                    Glide.with(context).load(product_image?.get(0)?.image_url)
                        .placeholder(R.color.dark_gray).into(binding.ivImage)
                    binding.tvDescription.text = product_description
                    binding.tvTitle.text = product_name
                    binding.tvPrice.text =cart_product_total_price + " So'm"
                }
            }
        }


    }


}