package com.ayizor.vodiypolymer.fragment


import android.annotation.SuppressLint
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.ayizor.vodiypolymer.R
import com.ayizor.vodiypolymer.adapter.CheckoutAdapter
import com.ayizor.vodiypolymer.databinding.ActivityCheckoutBinding
import com.ayizor.vodiypolymer.databinding.ItemSuccessfulDialogBinding
import com.ayizor.vodiypolymer.manager.UserPrefManager
import com.ayizor.vodiypolymer.model.Order
import com.ayizor.vodiypolymer.utils.Utils
import com.google.android.material.bottomnavigation.BottomNavigationView


class CheckoutFragment : Fragment() {

    lateinit var binding: ActivityCheckoutBinding
    val TAG: String = CheckoutFragment::class.java.simpleName
    private val args: CheckoutFragmentArgs by navArgs()
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
        refreshCartAdapter(args.orderList.ordersList)
    }

    private fun inits() {
        binding.rvCart.layoutManager = LinearLayoutManager(
            requireContext(),
            LinearLayoutManager.VERTICAL,
            false
        )
        binding.btnOrder.setOnClickListener {

            showDialog()
        }
        val user = UserPrefManager(
            requireContext()
        ).loadUser()

        binding.tvShippingAddressTitle.text = user.user_location?.get(0)?.location_name.toString()
        binding.tvShippingAddressFullAddress.text =
            user.user_location?.get(0)?.location_latitude?.toDouble()?.let {
                user.user_location[0].location_longitude?.toDouble()?.let { it1 ->
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

        }

        dialog.show()
    }


    private fun refreshCartAdapter(products: ArrayList<Order>) {
        val adapter = CheckoutAdapter(requireContext(), products)
        binding.rvCart.adapter = adapter
        binding.progressBar.visibility = View.GONE
        binding.rvCart.visibility = View.VISIBLE

    }
}