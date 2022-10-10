package uz.ayizor.vp.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import uz.ayizor.vp.utils.Logger
import uz.ayizor.vp.adapter.CartAdapter
import uz.ayizor.vp.databinding.FragmentCartBinding
import uz.ayizor.vp.manager.UserPrefManager
import uz.ayizor.vp.model.listmodel.OrdersList
import com.google.firebase.database.*
import com.google.firebase.database.annotations.NotNull
import uz.ayizor.vp.model.Cart
import uz.ayizor.vp.model.Location

class CartFragment : Fragment() {

    lateinit var binding: FragmentCartBinding
    val TAG: String = CartFragment::class.java.simpleName
    lateinit var product: Cart
    val productsList: ArrayList<Cart> = ArrayList()
    var totalPrice = 0
    lateinit var address: Location
    val addressList: ArrayList<Location> = ArrayList()
    lateinit var mContext: Context
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCartBinding.inflate(inflater, container, false)
        mContext= requireContext()
        inits()
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    private fun inits() {
        binding.rvCart.layoutManager = LinearLayoutManager(
            mContext,
            LinearLayoutManager.VERTICAL,
            false
        )

        binding.btnCheckout.setOnClickListener {
            getLocations(UserPrefManager(mContext).loadUser()?.user_id)
            if (UserPrefManager(mContext).loadUserLocations()?.isNotEmpty() == true) {
                val orders = OrdersList(productsList)
                val action = CartFragmentDirections.actionNavCartToCheckoutActivity(orders)
                findNavController().navigate(action)

            } else {
                val action = CartFragmentDirections.actionNavCartToShippingAddressFragment()
                findNavController().navigate(action)
            }
        }
        getProducts()

    }

    private fun refreshCartAdapter(products: ArrayList<Cart>) {
        val adapter = CartAdapter(mContext, products)
        binding.rvCart.adapter = adapter
        binding.progressBar.visibility = View.GONE
        binding.rvCart.visibility = View.VISIBLE

    }

    private fun getProducts() {
        binding.progressBar.visibility = View.VISIBLE
        binding.rvCart.visibility = View.GONE

        val reference = FirebaseDatabase.getInstance().getReference("carts")
        val query: Query = reference.orderByChild("cart_user_id")
                .equalTo(UserPrefManager(mContext).loadUser()?.user_id)
        query.addValueEventListener(object : ValueEventListener {
            @SuppressLint("SetTextI18n")
            override fun onDataChange(@NotNull snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    productsList.clear()

                    for (userSnapshot in snapshot.children) {
                        product = userSnapshot.getValue(Cart::class.java)!!
                        if (product != null) {
                            productsList.add(product)
                        }
                    }
                    if (productsList.isNotEmpty()){
                        refreshCartAdapter(productsList)
                        binding.tvTotalPrice.text = "$totalPrice So'm"
                        getTotalPrice(productsList)
                        binding.rlAddToCart.visibility = View.VISIBLE
                    }else{
                        binding.progressBar.visibility = View.GONE
                        binding.emptyState.llEmpty.visibility = View.VISIBLE
                    }


                }else{
                    binding.progressBar.visibility = View.GONE
                    binding.emptyState.llEmpty.visibility = View.VISIBLE
                }
            }

            override fun onCancelled(@NotNull error: DatabaseError) {}
        })


    }
    fun getLocations(userId: String?) {
        val reference = FirebaseDatabase.getInstance().reference
        val query: Query =
            reference.child("users").orderByChild("user_phone_number").equalTo(userId)
        query.addValueEventListener(object : ValueEventListener {
            @SuppressLint("SetTextI18n")
            override fun onDataChange(@NotNull snapshot: DataSnapshot) {
                if (snapshot.exists()) {


                    for (userSnapshot in snapshot.children) {
                        userSnapshot.ref.child("user_location")
                            .addValueEventListener(object : ValueEventListener {
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    if (snapshot.exists()) {


                                        for (locationSnapshot in snapshot.children) {
                                            Logger.e(TAG, "locationSnapshot: " + locationSnapshot.toString())
                                            address = locationSnapshot.getValue(Location::class.java)!!
                                            address.location_name?.let { Log.e(TAG, it) }
                                            addressList.add(address)
                                        }
                                    }



                                }

                                override fun onCancelled(error: DatabaseError) {
                                    TODO("Not yet implemented")
                                }

                            })


                    }

                } else {
                    Log.e(TAG, "NO DATA")
                }
            }

            override fun onCancelled(@NotNull error: DatabaseError) {
                Log.e(TAG, "NO DATA")
            }
        })

    }


    private fun getTotalPrice(products: ArrayList<Cart>) {
        Logger.e(TAG, "getTotalPrice")
        totalPrice = 0
        for (i in 0 until products.size) {

            val price = products[i].cart_product_total_price
            Logger.e(TAG, price.toString())
            if (price != null) {
                totalPrice += price.toInt()
            }
            Logger.e(TAG, totalPrice.toString())
        }
        binding.tvTotalPrice.text = totalPrice.toString() + " So'm"

    }


}