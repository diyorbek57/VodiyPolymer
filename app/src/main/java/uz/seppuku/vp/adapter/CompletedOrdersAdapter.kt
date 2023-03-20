package uz.seppuku.vp.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import uz.seppuku.vp.R
import uz.seppuku.vp.databinding.ItemOrderProductBinding
import uz.seppuku.vp.model.Order
import uz.seppuku.vp.model.Product


class CompletedOrdersAdapter(
    private val context: Context,
    private var orderList: ArrayList<Order>,
    private val onActionButtonClickListener: OnActionButtonClickListener
) : RecyclerView.Adapter<CompletedOrdersAdapter.CompletedOrdersHolder>() {


    val TAG: String = CompletedOrdersAdapter::class.java.simpleName
    val ref = FirebaseDatabase.getInstance().reference
    private lateinit var binding: ItemOrderProductBinding
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CompletedOrdersAdapter.CompletedOrdersHolder {

        binding = ItemOrderProductBinding.inflate(LayoutInflater.from(context), parent, false)
        return CompletedOrdersHolder(binding)


    }

    @SuppressLint("SetTextI18n", "NotifyDataSetChanged")
    override fun onBindViewHolder(
        holder: CompletedOrdersAdapter.CompletedOrdersHolder,
        @SuppressLint("RecyclerView") position: Int
    ) {
        val order: Order = orderList[position]


        holder.getProduct(order, order.order_product_id.toString(), holder)


    }


    override fun getItemCount(): Int {
        return orderList.size
    }


    inner class CompletedOrdersHolder(val binding: ItemOrderProductBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun getProduct(order: Order, product_id: String, holder: CompletedOrdersAdapter.CompletedOrdersHolder) {
            var product: Product? = null
            val productListener = object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    for (postSnapshot in dataSnapshot.children) {

                        product = postSnapshot.getValue(Product::class.java)

                    }
                    if (product != null) {
                        holder.bindOrder(order, product!!)
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
        fun bindOrder(order: Order, product: Product) {
            with(product) {

                with(order) {

                    binding.tvQuantity.text =
                        context.getString(R.string.quantity) + " = " + order_total_quantity
                    Glide.with(context).load(
                        product_image?.get(0)?.image_url
                    )
                        .placeholder(R.color.dark_gray).into(binding.ivImage)
                    binding.tvTitle.text = product_name
                    binding.tvPrice.text = "$product_price So'm"
                }
                binding.tvOrderAction.setOnClickListener {
                    onActionButtonClickListener.onActionButtonClickListener(order)
                }

                if (order.order_step == 3) {
                    binding.tvOrderAction.text = context.getString(R.string.leave_review)
                } else {
                    binding.tvOrderAction.text = context.getString(R.string.track_order)
                }
            }
        }
    }


    interface OnActionButtonClickListener {
        fun onActionButtonClickListener(order: Order)
    }
}