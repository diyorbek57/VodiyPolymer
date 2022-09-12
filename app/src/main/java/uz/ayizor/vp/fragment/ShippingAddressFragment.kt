package uz.ayizor.vp.fragment

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.database.*
import com.google.firebase.database.annotations.NotNull
import com.sucho.placepicker.AddressData
import com.sucho.placepicker.Constants
import com.sucho.placepicker.MapType
import com.sucho.placepicker.PlacePicker
import uz.ayizor.afeme.utils.Extensions.toast
import uz.ayizor.vp.R
import uz.ayizor.vp.adapter.EditShippingAddressAdapter
import uz.ayizor.vp.databinding.FragmentShippingAddressBinding
import uz.ayizor.vp.databinding.ItemAddLocationBottomsheetBinding
import uz.ayizor.vp.fragment.orders.OrderShippingAddressFragment
import uz.ayizor.vp.manager.UserPrefManager
import uz.ayizor.vp.model.Location
import uz.ayizor.vp.utils.Logger
import uz.ayizor.vp.utils.Utils


class ShippingAddressFragment : Fragment(), EditShippingAddressAdapter.OnItemClickListener {
    lateinit var binding: FragmentShippingAddressBinding
    val TAG: String = OrderShippingAddressFragment::class.java.simpleName
    val addressList: ArrayList<Location> = ArrayList()
    lateinit var address: Location
    lateinit var mContext: Context
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentShippingAddressBinding.inflate(inflater, container, false)
        mContext = requireContext()
        inits()

        return binding.root
    }

    private fun inits() {

        binding.rvAddress.layoutManager = LinearLayoutManager(
            mContext,
            LinearLayoutManager.VERTICAL,
            false
        )
        binding.btnAddNewAddress.setOnClickListener {
            openPlacePicker()
        }
        getAddresses()
    }

    private fun refreshAddressAdapter(products: ArrayList<Location>) {
        val adapter = EditShippingAddressAdapter(mContext, products, this)
        binding.rvAddress.adapter = adapter
//        binding.progressBar.visibility = View.GONE
//        binding.rvCart.visibility = View.VISIBLE

    }

    private fun openPlacePicker() {
        val intent = PlacePicker.IntentBuilder()
            .setLatLong(
                40.713852,
                72.054559
            )  // Initial Latitude and Longitude the Map will load into
            .showLatLong(true)  // Show Coordinates in the Activity
            .setMapZoom(17.0f)  // Map Zoom Level. Default: 14.0
            .setAddressRequired(true) // Set If return only Coordinates if cannot fetch Address for the coordinates. Default: True
            .hideMarkerShadow(true) // Hides the shadow under the map marker. Default: False
            // Change the default Marker Image
            .setMarkerImageImageColor(R.color.very_dark_gray_mostly_black)
            .setFabColor(R.color.very_dark_gray_mostly_black)
            .setPrimaryTextColor(R.color.white) // Change text color of Shortened Address
            .setSecondaryTextColor(R.color.white) // Change text color of full Address
            .setBottomViewColor(R.color.very_dark_gray_mostly_black) // Change Address View Background Color (Default: White)
            //Set Map Style (https://mapstyle.withgoogle.com/)
            .setMapType(MapType.NORMAL)
            .setPlaceSearchBar(
                false,
                Constants.GOOGLE_API_KEY
            ) //Activate GooglePlace Search Bar. Default is false/not activated. SearchBar is a chargeable feature by Google
            .onlyCoordinates(true)  //Get only Coordinates from Place Picker
            .hideLocationButton(false)   //Hide Location Button (Default: false)
            .disableMarkerAnimation(true)   //Disable Marker Animation (Default: false)
            .build(requireActivity())
        startActivityForResult(intent, Constants.PLACE_PICKER_REQUEST)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == Constants.PLACE_PICKER_REQUEST) {
            if (resultCode == Activity.RESULT_OK) {
                val addressData = data?.getParcelableExtra<AddressData>(Constants.ADDRESS_INTENT)
                showCreateBottomsheet(addressData)

            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun showCreateBottomsheet(addressData: AddressData?) {
        val sheetDialog = BottomSheetDialog(mContext, R.style.AppBottomSheetDialogTheme)
        val bottomSheetBinding: ItemAddLocationBottomsheetBinding =
            ItemAddLocationBottomsheetBinding.inflate(layoutInflater)
        sheetDialog.setContentView(bottomSheetBinding.root)
        val address = addressData?.longitude?.let {
            addressData.latitude.let { it1 ->
                Utils.getCoordinateName(
                    mContext,
                    it1, it
                )
            }
        }

        bottomSheetBinding.tvAddress.text = address?.address
        bottomSheetBinding.btnAdd.setOnClickListener {
            if (bottomSheetBinding.etSearch.text.isNullOrEmpty()) {
                createAddress(
                    Location(
                        Utils.getUUID(),
                        "Address",
                        addressData?.latitude.toString(),
                        addressData?.longitude.toString(),
                        bottomSheetBinding.isDefault.isChecked,
                        Utils.getCurrentTime(),
                        Utils.getCurrentTime()
                    )
                )
                sheetDialog.dismiss()
            } else {
                createAddress(
                    Location(
                        Utils.getUUID(),
                        bottomSheetBinding.etSearch.text.toString(),
                        addressData?.latitude.toString(),
                        addressData?.longitude.toString(),
                        bottomSheetBinding.isDefault.isChecked,
                        Utils.getCurrentTime(),
                        Utils.getCurrentTime()
                    )
                )
                sheetDialog.dismiss()
                getAddresses()
            }


        }

        sheetDialog.show();
        sheetDialog.window?.attributes?.windowAnimations = R.style.DialogAnimaton;
    }

    private fun showEditBottomsheet(location: Location) {
        val sheetDialog = BottomSheetDialog(mContext, R.style.AppBottomSheetDialogTheme)
        val bottomSheetBinding: ItemAddLocationBottomsheetBinding =
            ItemAddLocationBottomsheetBinding.inflate(layoutInflater)
        sheetDialog.setContentView(bottomSheetBinding.root)
        val address = location.location_longitude?.toDouble().let {
            location.location_latitude?.toDouble().let { it1 ->
                if (it1 != null) {
                    if (it != null) {
                        Utils.getCoordinateName(
                            mContext,
                            it1, it
                        )
                    }
                }
            }
        }

//        bottomSheetBinding.tvAddress.text = ad
//        bottomSheetBinding.btnAdd.setOnClickListener {
//            if (bottomSheetBinding.etSearch.text.isNullOrEmpty()) {
//                createAddress(
//                    Location(
//                        Utils.getUUID(),
//                        "Address",
//                        addressData?.latitude.toString(),
//                        addressData?.longitude.toString(),
//                        Utils.getCurrentTime(),
//                        Utils.getCurrentTime()
//                    )
//                )
//                sheetDialog.dismiss()
//            } else {
//                createAddress(
//                    Location(
//                        Utils.getUUID(),
//                        bottomSheetBinding.etSearch.text.toString(),
//                        addressData?.latitude.toString(),
//                        addressData?.longitude.toString(),
//                        Utils.getCurrentTime(),
//                        Utils.getCurrentTime()
//                    )
//                )
//                sheetDialog.dismiss()
//                getAddresses()
//            }
//
//
//        }

        sheetDialog.show();
        sheetDialog.window?.attributes?.windowAnimations = R.style.DialogAnimaton;
    }


    private fun getAddresses() {
        val reference = FirebaseDatabase.getInstance().reference
        val query: Query =
            reference.child("users").orderByChild("user_id")
                .equalTo(UserPrefManager(mContext).loadUser()?.user_id)
        query.addValueEventListener(object : ValueEventListener {
            @SuppressLint("SetTextI18n")
            override fun onDataChange(@NotNull snapshot: DataSnapshot) {
                if (snapshot.exists()) {

                    for (userSnapshot in snapshot.children) {
                        userSnapshot.ref.child("user_location")
                            .addValueEventListener(object : ValueEventListener {
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    for (locationSnapshot in snapshot.children) {
                                        Logger.e(
                                            TAG,
                                            "locationSnapshot: " + locationSnapshot.toString()
                                        )
                                        address =
                                            locationSnapshot.getValue(Location::class.java)!!


                                        address.location_name?.let { Log.e(TAG, it) }
                                        addressList.add(address)
                                    }
                                    refreshAddressAdapter(addressList)
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

    private fun createAddress(location: Location) {

        val reference = FirebaseDatabase.getInstance().getReference("users")
        val query: Query = reference.orderByChild("user_id")
            .equalTo(UserPrefManager(mContext).loadUser()?.user_id)
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            @SuppressLint("SetTextI18n")
            override fun onDataChange(@NotNull snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (userSnapshot in snapshot.children) {
                        userSnapshot.ref.child("user_location").push().setValue(location)

                    }
                }
            }

            override fun onCancelled(@NotNull error: DatabaseError) {}
        })
    }

    override fun onItemClickListener(location: Location) {
        toast(location.location_id.toString())
    }


}