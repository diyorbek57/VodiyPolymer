package com.ayizor.vodiypolymer.fragment.orders

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.ayizor.afeme.utils.Extensions.toast
import com.ayizor.vodiypolymer.adapter.OrderAdapter
import com.ayizor.vodiypolymer.databinding.FragmentOngoingBinding
import com.ayizor.vodiypolymer.manager.UserPrefManager
import com.ayizor.vodiypolymer.model.Order
import com.google.firebase.database.*
import com.google.firebase.database.annotations.NotNull

class OngoingOrdersFragment : Fragment(), OrderAdapter.OnActionButtonClickListener {

    lateinit var binding: FragmentOngoingBinding
    val TAG: String = OngoingOrdersFragment::class.java.simpleName
    lateinit var product: Order
    val productsList: ArrayList<Order> = ArrayList()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentOngoingBinding.inflate(inflater, container, false)
        inits()
        return binding.root
    }

    private fun inits() {
        binding.rvOngoing.layoutManager = LinearLayoutManager(
            requireContext(),
            LinearLayoutManager.VERTICAL,
            false
        )
        getOrders()
    }


    private fun getOrders() {
        val reference = FirebaseDatabase.getInstance().getReference("orders")
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
                            if (product.product_step!! > 0 && product.product_isOrdered)
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
        binding.rvOngoing.adapter = adapter
        binding.progressBar.visibility = View.GONE
        binding.rvOngoing.visibility = View.VISIBLE

    }

    override fun onActionButtonClickListener(order: Order) {
        toast(order.product_id.toString())
    }


}