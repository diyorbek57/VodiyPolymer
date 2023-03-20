package uz.seppuku.vp.fragment.orders

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.database.*
import com.google.firebase.database.annotations.NotNull
import uz.seppuku.vp.adapter.CompletedOrdersAdapter
import uz.seppuku.vp.databinding.FragmentCompletedBinding
import uz.seppuku.vp.databinding.ItemLeaveReviewBorromsheetBinding
import uz.seppuku.vp.manager.UserPrefManager
import uz.seppuku.vp.model.Order
import uz.seppuku.vp.model.Product
import uz.seppuku.vp.utils.Logger
import uz.seppuku.vp.R

class CompletedOrdersFragment : Fragment(), CompletedOrdersAdapter.OnActionButtonClickListener {

    lateinit var binding: FragmentCompletedBinding
    val TAG: String = CompletedOrdersFragment::class.java.simpleName
    lateinit var product: Order
    val productsList: ArrayList<Order> = ArrayList()
    lateinit var mContext: Context
    val ref = FirebaseDatabase.getInstance().reference
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCompletedBinding.inflate(inflater, container, false)
        mContext = requireContext()
        inits()

        return binding.root
    }


    private fun inits() {
        binding.rvCompletedOrders.layoutManager = LinearLayoutManager(
            mContext,
            LinearLayoutManager.VERTICAL,
            false
        )
        getOrders()
    }

    private fun getOrders() {
        val reference = FirebaseDatabase.getInstance().getReference("orders")
        val query: Query =
            reference.orderByChild("order_user_id")
                .equalTo(UserPrefManager(mContext).loadUser()?.user_id)
        query.addValueEventListener(object : ValueEventListener {
            @SuppressLint("SetTextI18n")
            override fun onDataChange(@NotNull snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    productsList.clear()
                    for (userSnapshot in snapshot.children) {

                        product = userSnapshot.getValue(Order::class.java)!!
                        Logger.e(TAG,product.toString())
                        if (product != null) {
                            if (product.order_step == 3 && product.order_isOrdered)
                                productsList.add(product)
                        }

                    }
                    if (productsList.isNotEmpty()) {
                        refreshOrdersAdapter(productsList)
                    } else {
                        binding.progressBar.visibility = View.GONE
                        binding.emptyState.llEmpty.visibility = View.VISIBLE
                    }


                } else {
                    binding.progressBar.visibility = View.GONE
                    binding.emptyState.llEmpty.visibility = View.VISIBLE
                }
            }

            override fun onCancelled(@NotNull error: DatabaseError) {
                Logger.e(TAG, "message: " + error.message + "code: " + error.code)
                binding.progressBar.visibility = View.GONE
                binding.emptyState.llEmpty.visibility = View.VISIBLE
            }
        })
    }

    private fun refreshOrdersAdapter(orders: ArrayList<Order>) {
        val adapter = CompletedOrdersAdapter(mContext, orders, this)
        binding.rvCompletedOrders.adapter = adapter
        binding.progressBar.visibility = View.GONE
        binding.rvCompletedOrders.visibility =View.VISIBLE

    }

    override fun onActionButtonClickListener(order: Order) {
        getProduct(order)
    }

    @SuppressLint("SetTextI18n")
    private fun showReviewBottomSheet(order: Order,product: Product) {
        with(product){

        with(order){


        val sheetDialog = BottomSheetDialog(mContext, R.style.AppBottomSheetDialogTheme)
        val bottomSheetBinding: ItemLeaveReviewBorromsheetBinding =
            ItemLeaveReviewBorromsheetBinding.inflate(layoutInflater)
        sheetDialog.setContentView(bottomSheetBinding.root)

        bottomSheetBinding.tvPrice.text = "$order_total_price So'm"
        bottomSheetBinding.tvQuantity.text =
            getString(R.string.quantity) + " = " + order_total_quantity
        Glide.with(mContext).load(product_image?.get(0)?.image_url)
            .into(bottomSheetBinding.ivImage)
        bottomSheetBinding.tvTitle.text = product_name

        bottomSheetBinding.btnSubmit.setOnClickListener {

            sheetDialog.dismiss()
        }

        sheetDialog.show();
        sheetDialog.window?.attributes?.windowAnimations = R.style.DialogAnimaton;
        }
        }
    }

    private fun getProduct(order: Order) {
        var product: Product? = null
        val productListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (postSnapshot in dataSnapshot.children) {

                    product = postSnapshot.getValue(Product::class.java)

                }
                if (product != null) {
                    showReviewBottomSheet(order,product!!)
                }


            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost: onCancelled", databaseError.toException())
            }
        }
        ref.child("products").orderByChild("product_id").equalTo(order.order_product_id)
            .addValueEventListener(productListener)
    }
}