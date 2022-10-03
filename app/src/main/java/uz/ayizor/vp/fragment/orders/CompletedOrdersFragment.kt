package uz.ayizor.vp.fragment.orders

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import uz.ayizor.vp.utils.Logger
import uz.ayizor.vp.adapter.OrderAdapter
import uz.ayizor.vp.databinding.FragmentCompletedBinding
import uz.ayizor.vp.databinding.ItemLeaveReviewBorromsheetBinding
import uz.ayizor.vp.manager.UserPrefManager
import uz.ayizor.vp.model.Order
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.database.*
import com.google.firebase.database.annotations.NotNull
import uz.ayizor.vp.R
import uz.ayizor.vp.model.Cart
import uz.ayizor.vp.model.Product

class CompletedOrdersFragment : Fragment(), OrderAdapter.OnActionButtonClickListener {

    lateinit var binding: FragmentCompletedBinding
    val TAG: String = OngoingOrdersFragment::class.java.simpleName
    lateinit var product: Order
    val productsList: ArrayList<Order> = ArrayList()
    lateinit var mContext: Context
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCompletedBinding.inflate(inflater, container, false)
        mContext= requireContext()
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
        val reference = FirebaseDatabase.getInstance().getReference("carts")
        val query: Query =
            reference.orderByChild("product_user_id")
                .equalTo(UserPrefManager(mContext).loadUser()?.user_id)
        query.addValueEventListener(object : ValueEventListener {
            @SuppressLint("SetTextI18n")
            override fun onDataChange(@NotNull snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    productsList.clear()
                    for (userSnapshot in snapshot.children) {

                        product = userSnapshot.getValue(Order::class.java)!!
                        if (product != null) {
                            if (product.order_step == 3 && product.order_isOrdered)
                                productsList.add(product)
                        }

                    }
                    if(productsList.isNotEmpty()){
                        refreshOrdersAdapter(productsList)
                    }else{
                        binding.progressBar.visibility = View.GONE
                        binding.emptyState.llEmpty.visibility = View.VISIBLE
                    }


                }else{
                    binding.progressBar.visibility = View.GONE
                    binding.emptyState.llEmpty.visibility = View.VISIBLE
                }
            }

            override fun onCancelled(@NotNull error: DatabaseError) {
Logger.e(TAG,"message: "+error.message + "code: "+error.code)
                binding.progressBar.visibility = View.GONE
                binding.emptyState.llEmpty.visibility = View.VISIBLE
            }
        })
    }

    private fun refreshOrdersAdapter(products: ArrayList<Order>) {
        val adapter = OrderAdapter(mContext, products, this)
        binding.rvCompletedOrders.adapter = adapter
        binding.progressBar.visibility = View.GONE

    }

    override fun onActionButtonClickListener(product: Cart, order_step:Int) {
        showReviewBottomSheet(product)
    }

    @SuppressLint("SetTextI18n")
    private fun showReviewBottomSheet(product: Cart) {
        val sheetDialog = BottomSheetDialog(mContext, R.style.AppBottomSheetDialogTheme)
        val bottomSheetBinding: ItemLeaveReviewBorromsheetBinding =
            ItemLeaveReviewBorromsheetBinding.inflate(layoutInflater)
        sheetDialog.setContentView(bottomSheetBinding.root)

        bottomSheetBinding.tvPrice.text = product.cart_product_total_price + " So'm"
        bottomSheetBinding.tvQuantity.text =
            getString(R.string.quantity) + " = " + product.cart_product_total_quantity
        Glide.with(mContext).load(product.cart_product?.product_image?.get(0)?.image_url)
            .into(bottomSheetBinding.ivImage)
        bottomSheetBinding.tvTitle.text = product.cart_product?.product_name

        bottomSheetBinding.btnSubmit.setOnClickListener {

            sheetDialog.dismiss()
        }

        sheetDialog.show();
        sheetDialog.window?.attributes?.windowAnimations = R.style.DialogAnimaton;

    }
}