package uz.ayizor.vp.fragment


import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.*
import com.google.firebase.database.annotations.NotNull
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import uz.ayizor.vp.R
import uz.ayizor.vp.adapter.CheckoutAdapter
import uz.ayizor.vp.databinding.ActivityCheckoutBinding
import uz.ayizor.vp.databinding.ItemSuccessfulDialogBinding
import uz.ayizor.vp.fragment.orders.OrderShippingAddressFragment
import uz.ayizor.vp.manager.UserPrefManager
import uz.ayizor.vp.model.Cart
import uz.ayizor.vp.model.Location
import uz.ayizor.vp.model.Order
import uz.ayizor.vp.utils.Logger
import uz.ayizor.vp.utils.Utils


class CheckoutFragment : Fragment(R.layout.activity_checkout) {

    lateinit var binding: ActivityCheckoutBinding
    val TAG: String = CheckoutFragment::class.java.simpleName
    private val args: CheckoutFragmentArgs by navArgs()
    private lateinit var database: DatabaseReference
    var productsList: ArrayList<Cart> = ArrayList()
    lateinit var selectedLocationId: String
    lateinit var location: Location
    lateinit var order: Order
    lateinit var mContext: Context
    lateinit var user_id: String
    val reference = FirebaseDatabase.getInstance().reference
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = ActivityCheckoutBinding.bind(view)
        mContext = requireContext()
        user_id = UserPrefManager(mContext).loadUser()?.user_id.toString()
        setFragmentResultListener(OrderShippingAddressFragment.REQUEST_KEY){ key, bundle ->
            selectedLocationId = bundle.getString(key).toString()
            getSelectedLocation(selectedLocationId)
        }
        getOrderList()
        getDefaultAddress()
        inits()
    }

    private fun getSelectedLocation(id: String) {
        val query: Query =
            reference.child("users_locations").orderByChild("location_id")
                .equalTo(id)
        query.addValueEventListener(object : ValueEventListener {
            @SuppressLint("SetTextI18n")
            override fun onDataChange(@NotNull snapshot: DataSnapshot) {
                if (snapshot.exists()) {

                    for (userSnapshot in snapshot.children) {
                        userSnapshot.ref.child("user_location").orderByChild("location_isDefault")
                            .equalTo(true)
                            .addValueEventListener(object : ValueEventListener {
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    if (snapshot.exists()) {
                                        for (locationSnapshot in snapshot.children) {
                                            Logger.e(
                                                TAG,
                                                "locationSnapshot: " + locationSnapshot.toString()
                                            )
                                            location =
                                                locationSnapshot.getValue(Location::class.java)!!

                                        }
                                        displayLocation(location)
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


    private fun getOrderList() {
        productsList = args.orderList.ordersList
        refreshCartAdapter(args.orderList.ordersList)
    }

    private fun inits() {


        database = Firebase.database.reference
        binding.rvCart.layoutManager = LinearLayoutManager(
            requireContext(),
            LinearLayoutManager.VERTICAL,
            false
        )


        binding.btnOrder.setOnClickListener {

            for (i in 0 until productsList.size) {
                order = Order(
                    Utils.getUUID(),
                    user_id,
                    location.location_id,
                    productsList[i].cart_product_id,
                    true,
                    1,
                    productsList[0].cart_product_total_price,
                    productsList[0].cart_product_total_quantity,
                    Utils.getCurrentTime(),
                    Utils.getCurrentTime()
                )

                database.child("orders").push().setValue(order).addOnSuccessListener {
                    productsList[i].cart_id?.let { it1 -> deleteProduct(it1) }
                }
            }


            showDialog()
        }

        binding.rlChangeLocations.setOnClickListener {

            val action =
                CheckoutFragmentDirections.actionCheckoutActivityToOrderShippingAddressFragment()
            findNavController().navigate(action)
        }


    }

    private fun getDefaultAddress() {
        val query: Query =
            reference.child("users").orderByChild("user_id")
                .equalTo(user_id)
        query.addValueEventListener(object : ValueEventListener {
            @SuppressLint("SetTextI18n")
            override fun onDataChange(@NotNull snapshot: DataSnapshot) {
                if (snapshot.exists()) {

                    for (userSnapshot in snapshot.children) {
                        userSnapshot.ref.child("user_location").orderByChild("location_isDefault")
                            .equalTo(true)
                            .addValueEventListener(object : ValueEventListener {
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    if (snapshot.exists()) {
                                        for (locationSnapshot in snapshot.children) {
                                            Logger.e(
                                                TAG,
                                                "locationSnapshot: " + locationSnapshot.toString()
                                            )
                                            location =
                                                locationSnapshot.getValue(Location::class.java)!!

                                        }
                                        displayLocation(location)
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

    private fun displayLocation(location: Location) {
        val detectedLocation = Utils.getCoordinateName(
            mContext,
            location.location_latitude!!.toDouble(),
            location.location_longitude!!.toDouble()
        )
        binding.tvShippingAddressFullAddress.text = detectedLocation?.knownName
        binding.tvShippingAddressTitle.text = location.location_name
    }

    @SuppressLint("SetTextI18n")
    private fun showDialog() {
        // Inflate dialog main
        val dialogBinding = ItemSuccessfulDialogBinding.inflate(layoutInflater);

        // Initialize dialog
        val dialog: Dialog = Dialog(requireContext());

        // set background transparent
        dialog.window?.setBackgroundDrawable(
            ColorDrawable(
                Color.TRANSPARENT
            )
        );
        // set view
        dialog.setContentView(dialogBinding.root);

        dialogBinding.tvTitle.text = getString(R.string.order_successful)
        dialogBinding.tvMessage.text = getString(R.string.you_have_successfully_made_order)
        dialogBinding.btnViewOrder.setOnClickListener {
            findNavController().navigate(CheckoutFragmentDirections.actionCheckoutActivityToNavOrders())
            dialog.dismiss()
        }

        dialog.show()
    }


    private fun refreshCartAdapter(products: ArrayList<Cart>) {
        val adapter = CheckoutAdapter(requireContext(), products)
        binding.rvCart.adapter = adapter
        binding.progressBar.visibility = View.GONE
        binding.rvCart.visibility = View.VISIBLE

    }

    private fun deleteProduct(id: String) {

        val applesQuery: Query =
            database.child("carts").orderByChild("cart_id").equalTo(id)

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
}