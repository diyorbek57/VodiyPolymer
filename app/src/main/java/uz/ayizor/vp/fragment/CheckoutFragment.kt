package uz.ayizor.vp.fragment


import android.annotation.SuppressLint
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import uz.ayizor.vp.R
import uz.ayizor.vp.adapter.CheckoutAdapter
import uz.ayizor.vp.databinding.ActivityCheckoutBinding
import uz.ayizor.vp.databinding.ItemSuccessfulDialogBinding
import uz.ayizor.vp.manager.UserPrefManager
import uz.ayizor.vp.model.Location
import uz.ayizor.vp.model.Order
import uz.ayizor.vp.model.User
import uz.ayizor.vp.utils.Utils


class CheckoutFragment : Fragment() {

    lateinit var binding: ActivityCheckoutBinding
    val TAG: String = CheckoutFragment::class.java.simpleName
    private val args: CheckoutFragmentArgs by navArgs()
    private lateinit var database: DatabaseReference
    var productsList: ArrayList<Order> = ArrayList()
    lateinit var selectedLocation: Location
    var user: User? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = ActivityCheckoutBinding.inflate(inflater, container, false)
        getOrderList()
        inits()
        return binding.root
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
            selectedLocation = user?.user_location?.get(0)!!
            for (i in 0 until productsList.size) {
                productsList[i].product_location = selectedLocation
                productsList[i].product_isOrdered = true
                productsList[i].product_step = 1
                database.child("orders").push().setValue(productsList[i]).addOnSuccessListener {
                    deleteProduct(productsList[i].product_id.toString())
                }
            }
            showDialog()
        }
        user = UserPrefManager(
            requireContext()
        ).loadUser()

        binding.tvShippingAddressTitle.text = user?.user_location?.get(0)?.location_name.toString()
        binding.tvShippingAddressFullAddress.text =
            user?.user_location?.get(0)?.location_latitude?.toDouble()?.let {
                user?.user_location!![0].location_longitude?.toDouble()?.let { it1 ->
                    Utils.getCoordinateName(
                        requireContext(),
                        it,
                        it1
                    )?.knownName.toString()
                }
            }

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


    private fun refreshCartAdapter(products: ArrayList<Order>) {
        val adapter = CheckoutAdapter(requireContext(), products)
        binding.rvCart.adapter = adapter
        binding.progressBar.visibility = View.GONE
        binding.rvCart.visibility = View.VISIBLE

    }

    private fun deleteProduct( id: String) {

        val applesQuery: Query =
            database.child("carts").orderByChild("product_id").equalTo(id)

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