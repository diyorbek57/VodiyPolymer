package com.ayizor.vodiypolymer.fragment.orders

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.ayizor.vodiypolymer.R
import com.ayizor.vodiypolymer.adapter.OrderAdapter
import com.ayizor.vodiypolymer.databinding.FragmentCompletedBinding
import com.ayizor.vodiypolymer.databinding.ItemLeaveReviewBorromsheetBinding
import com.ayizor.vodiypolymer.manager.UserPrefManager
import com.ayizor.vodiypolymer.model.Order
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.database.*
import com.google.firebase.database.annotations.NotNull


class CompletedOrdersFragment : Fragment(), OrderAdapter.OnActionButtonClickListener {

    lateinit var binding: FragmentCompletedBinding
    val TAG: String = OngoingOrdersFragment::class.java.simpleName
    lateinit var product: Order
    val productsList: ArrayList<Order> = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCompletedBinding.inflate(inflater, container, false)
        inits()
        return binding.root
    }


    private fun inits() {
        binding.rvCompletedOrders.layoutManager = LinearLayoutManager(
            requireContext(),
            LinearLayoutManager.VERTICAL,
            false
        )
        getOrders()
    }

    private fun getOrders() {
        val reference = FirebaseDatabase.getInstance().getReference("carts")
        val query: Query =
            reference.orderByChild("product_user_id")
                .equalTo(UserPrefManager(requireContext()).loadUser().user_id)
        query.addValueEventListener(object : ValueEventListener {
            @SuppressLint("SetTextI18n")
            override fun onDataChange(@NotNull snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    productsList.clear()
                    for (userSnapshot in snapshot.children) {

                        product = userSnapshot.getValue(Order::class.java)!!
                        if (product != null) {
                            if (product.product_step == 4 && product.product_isOrdered)
                                productsList.add(product)
                        }
                        refreshOrdersAdapter(productsList)
                    }

                }
            }

            override fun onCancelled(@NotNull error: DatabaseError) {}
        })
    }

    private fun refreshOrdersAdapter(products: ArrayList<Order>) {
        val adapter = OrderAdapter(requireContext(), products, this)
        binding.rvCompletedOrders.adapter = adapter
        binding.progressBar.visibility = View.GONE
        binding.rvCompletedOrders.visibility = View.VISIBLE

    }

    override fun onActionButtonClickListener(order: Order) {
        showReviewBottomSheet(order)
    }

    @SuppressLint("SetTextI18n")
    private fun showReviewBottomSheet(order: Order) {
        val sheetDialog = BottomSheetDialog(requireContext(), R.style.AppBottomSheetDialogTheme)
        val bottomSheetBinding: ItemLeaveReviewBorromsheetBinding =
            ItemLeaveReviewBorromsheetBinding.inflate(layoutInflater)
        sheetDialog.setContentView(bottomSheetBinding.root)

        bottomSheetBinding.tvPrice.text = order.product_total_price + " So'm"
        bottomSheetBinding.tvQuantity.text =
            getString(R.string.quantity) + " = " + order.product_total_quantity
        Glide.with(requireContext()).load(order.product_image?.get(0)?.image_url)
            .into(bottomSheetBinding.ivImage)
        bottomSheetBinding.tvTitle.text = order.product_name

        bottomSheetBinding.btnSubmit.setOnClickListener {

            sheetDialog.dismiss()
        }

        sheetDialog.show();
        sheetDialog.window?.attributes?.windowAnimations = R.style.DialogAnimaton;

    }
}