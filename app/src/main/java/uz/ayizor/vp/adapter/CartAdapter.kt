package uz.ayizor.vp.adapter

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.Window
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.database.*
import com.mcdev.quantitizerlibrary.AnimationStyle
import com.mcdev.quantitizerlibrary.HorizontalQuantitizer
import com.mcdev.quantitizerlibrary.QuantitizerListener
import uz.ayizor.vp.R
import uz.ayizor.vp.databinding.ItemCartBinding
import uz.ayizor.vp.model.Cart
import uz.ayizor.vp.model.Product

open class CartAdapter(
    val context: Context,
    var postsList: ArrayList<Cart>
) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {


    val TAG: String = CartAdapter::class.java.simpleName
    val ref = FirebaseDatabase.getInstance().reference
    private lateinit var binding: ItemCartBinding
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {

        binding = ItemCartBinding.inflate(LayoutInflater.from(context), parent, false)
        return CartViewHolder(binding)


    }

    @SuppressLint("SetTextI18n", "NotifyDataSetChanged")
    override fun onBindViewHolder(
        holder: CartViewHolder,
        @SuppressLint("RecyclerView") position: Int
    ) {

        val product: Cart = postsList[position]
        holder.getProduct(product, product.cart_product_id.toString(), holder)


    }

    private fun deleteProduct(position: Int, id: String) {

        val applesQuery: Query =
            ref.child("carts").orderByChild("product_id").equalTo(id)

        applesQuery.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (appleSnapshot in dataSnapshot.children) {
                    appleSnapshot.ref.removeValue()
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        })
    }

    fun setupQuantityStepper(quantityStepper: HorizontalQuantitizer, product: Cart) {
        quantityStepper.textAnimationStyle = AnimationStyle.SWING
        quantityStepper.isReadOnly = false
        quantityStepper.setValueBackgroundColor(R.color.gray)
        quantityStepper.setMinusIconColor(R.color.very_dark_gray_mostly_black)
        quantityStepper.setPlusIconColor(R.color.very_dark_gray_mostly_black)
        quantityStepper.setPlusIconBackgroundColor(R.color.gray)
        quantityStepper.setMinusIconBackgroundColor(R.color.gray)
        quantityStepper.minValue = 1
        quantityStepper.maxValue = 255
        quantityStepper.setIconWidthAndHeight(45, 45)
        quantityStepper.value = product.cart_product_total_quantity?.toInt()!!
        quantityStepper.setQuantitizerListener(object : QuantitizerListener {
            override fun onIncrease() {
            }

            override fun onDecrease() {

            }

            override fun onValueChanged(value: Int) {
                changeQuantity(value, product.cart_id)
            }
        })
    }

    private fun changeQuantity(value: Int, productId: String?) {
        val applesQuery: Query =
            ref.child("carts").orderByChild("cart_id").equalTo(productId)

        applesQuery.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (appleSnapshot in dataSnapshot.children) {
                    appleSnapshot.ref.child("cart_product_total_quantity")
                        .setValue(value.toString())
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        })
    }

    fun showDialogDouble(position: Int, id: String?) {
        val dialog = Dialog(context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.view_dialog_double)
        dialog.setCanceledOnTouchOutside(true)
        val d_title = dialog.findViewById<TextView>(R.id.d_title)
        val d_message = dialog.findViewById<TextView>(R.id.d_message)
        val d_confirm = dialog.findViewById<TextView>(R.id.d_confirm)
        val d_cancel = dialog.findViewById<TextView>(R.id.d_cancel)
        d_title.text = "Remove from cart"
        d_message.text = "Do you want to remove the product in the cart?"
        d_confirm.setOnClickListener {
            if (id != null) {
                postsList.remove(postsList.get(position))
                notifyDataSetChanged()
                deleteProduct(position, id)
            }
            dialog.dismiss()

        }
        d_cancel.setOnClickListener {
            dialog.dismiss()

        }
        dialog.show()
    }

    override fun getItemCount(): Int {
        return postsList.size
    }


    inner class CartViewHolder(val binding: ItemCartBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun getProduct(cart: Cart, product_id: String, holder: CartViewHolder) {
            var product: Product? = null
            val productListener = object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    for (postSnapshot in dataSnapshot.children) {

                        product = postSnapshot.getValue(Product::class.java)

                    }
                    if (product != null) {
                        holder.bindCart(cart, product!!)
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
        fun bindCart(cart: Cart, product: Product) {

            with(product) {
                with(cart) {
                    binding.ivDelete.setOnClickListener {
                        if (cart_id != null) {
                            showDialogDouble(position, cart_id)
                        }
                    }
                    Glide.with(context).load(product_image?.get(0)?.image_url)
                        .placeholder(R.color.dark_gray).into(binding.ivImage)
                    binding.tvDescription.text = product_description
                    binding.tvTitle.text = product_name
                    binding.tvPrice.text = "$cart_product_total_price So'm"
                    setupQuantityStepper(binding.quantityStepper, cart)

                }
            }

        }
    }


}