package uz.ayizor.vp.adapter

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import uz.ayizor.vp.model.Order
import com.bumptech.glide.Glide
import com.google.firebase.database.*
import com.mcdev.quantitizerlibrary.AnimationStyle
import com.mcdev.quantitizerlibrary.HorizontalQuantitizer
import com.mcdev.quantitizerlibrary.QuantitizerListener
import uz.ayizor.vp.R

class CartAdapter(
    val context: Context,
    var postsList: ArrayList<Order>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    val TAG: String = CartAdapter::class.java.simpleName
    val ref = FirebaseDatabase.getInstance().reference
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_cart, parent, false)
        return CartViewHolder(view)


    }

    @SuppressLint("SetTextI18n", "NotifyDataSetChanged")
    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        @SuppressLint("RecyclerView") position: Int
    ) {

        val product: Order = postsList[position]
        if (holder is CartViewHolder) {
            // holder.color.setBackgroundColor(Color.parseColor("#" + product.product_color))

            holder.delete.setOnClickListener {
                if (product.product_id != null) {
                    showDialogDouble(position, product.product_id)
                }
            }
            Glide.with(context).load(product.product_image?.get(0)?.image_url)
                .placeholder(R.color.dark_gray).into(holder.image)
            holder.description.text = product.product_description
            holder.title.text = product.product_name
            holder.total_price.text = product.product_total_price+" So'm"
            setupQuantityStepper(holder.quantitizer, product)
        }


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

    private fun setupQuantityStepper(quantityStepper: HorizontalQuantitizer, product: Order) {
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
        quantityStepper.value = product.product_total_quantity?.toInt()!!
        quantityStepper.setQuantitizerListener(object : QuantitizerListener {
            override fun onIncrease() {
            }

            override fun onDecrease() {

            }

            override fun onValueChanged(value: Int) {
                changeQuantity(value, product.product_id)
            }
        })
    }

    private fun changeQuantity(value: Int, productId: String?) {
        val applesQuery: Query =
            ref.child("carts").orderByChild("product_id").equalTo(productId)

        applesQuery.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (appleSnapshot in dataSnapshot.children) {
                    appleSnapshot.ref.child("product_total_quantity").setValue(value.toString())
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


    class CartViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var image: ImageView
        var delete: ImageView
        var mainRl: CardView
        var title: TextView
        var description: TextView
        var total_price: TextView
        var quantitizer: HorizontalQuantitizer


        init {
            image = itemView.findViewById(R.id.iv_image)
            delete = itemView.findViewById(R.id.iv_delete)
            mainRl = itemView.findViewById(R.id.ll_main)
            title = itemView.findViewById(R.id.tv_title)
            description = itemView.findViewById(R.id.tv_description)
            total_price = itemView.findViewById(R.id.tv_price)
            quantitizer = itemView.findViewById(R.id.quantity_stepper)
        }
    }


}