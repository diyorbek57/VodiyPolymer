package com.ayizor.vodiypolymer.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.ayizor.afeme.utils.Logger
import com.ayizor.vodiypolymer.activity.ShippingAddressActivity
import com.ayizor.vodiypolymer.adapter.CartAdapter
import com.ayizor.vodiypolymer.databinding.FragmentCartBinding
import com.ayizor.vodiypolymer.manager.UserPrefManager
import com.ayizor.vodiypolymer.model.Order
import com.ayizor.vodiypolymer.model.listmodel.OrdersList
import com.google.firebase.database.*
import com.google.firebase.database.annotations.NotNull
import java.io.Serializable


class CartFragment : Fragment() {

    lateinit var binding: FragmentCartBinding
    val TAG: String = CartFragment::class.java.simpleName
    lateinit var product: Order
    val productsList: ArrayList<Order> = ArrayList()
    var totalPrice = 0
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCartBinding.inflate(inflater, container, false)
        inits()
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    private fun inits() {
        binding.rvCart.layoutManager = LinearLayoutManager(
            requireContext(),
            LinearLayoutManager.VERTICAL,
            false
        )

        binding.btnCheckout.setOnClickListener {
            if (!UserPrefManager(requireContext()).loadUser().user_location?.get(0)?.location_id.isNullOrEmpty()) {
                val orders= OrdersList(productsList)
                val action = CartFragmentDirections.actionNavCartToCheckoutActivity(orders)
                findNavController().navigate(action)

            } else {
                val intent = Intent(requireContext(), ShippingAddressActivity::class.java)
                startActivity(intent)
            }
        }
        getProducts()

    }

    private fun refreshCartAdapter(products: ArrayList<Order>) {
        val adapter = CartAdapter(requireContext(), products)
        binding.rvCart.adapter = adapter
        binding.progressBar.visibility = View.GONE
        binding.rvCart.visibility = View.VISIBLE

    }

    private fun getProducts() {
        binding.progressBar.visibility = View.VISIBLE
        binding.rvCart.visibility = View.GONE

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
                            Log.e(TAG, product.toString())
                            Log.e(TAG, product.product_total_price.toString())
                            productsList.add(product)
                        }
                    }
                    binding.tvTotalPrice.text = "$totalPrice So'm"
                    getTotalPrice(productsList)
                    refreshCartAdapter(productsList)
                }
            }

            override fun onCancelled(@NotNull error: DatabaseError) {}
        })


    }

    private fun getTotalPrice(products: ArrayList<Order>) {
        Logger.e(TAG, "getTotalPrice")
        totalPrice=0
        for (i in 0 until products.size) {

            val price = products[i].product_total_price
            Logger.e(TAG, price.toString())
            if (price != null) {
                totalPrice += price.toInt()
            }
            Logger.e(TAG, totalPrice.toString())
        }
        binding.tvTotalPrice.text = totalPrice.toString()+" So'm"

    }


}