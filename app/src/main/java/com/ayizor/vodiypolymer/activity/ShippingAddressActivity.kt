package com.ayizor.vodiypolymer.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.ayizor.afeme.utils.Extensions.toast
import com.ayizor.afeme.utils.Logger
import com.ayizor.vodiypolymer.R
import com.ayizor.vodiypolymer.adapter.ShippingAddressAdapter
import com.ayizor.vodiypolymer.databinding.ActivityShippingAddressBinding
import com.ayizor.vodiypolymer.databinding.ItemAddLocationBottomsheetBinding
import com.ayizor.vodiypolymer.manager.UserPrefManager
import com.ayizor.vodiypolymer.model.Location
import com.ayizor.vodiypolymer.utils.Utils
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.database.*
import com.google.firebase.database.annotations.NotNull
import com.sucho.placepicker.AddressData
import com.sucho.placepicker.Constants
import com.sucho.placepicker.Constants.GOOGLE_API_KEY
import com.sucho.placepicker.MapType
import com.sucho.placepicker.PlacePicker

class ShippingAddressActivity : BaseActivity(),
    ShippingAddressAdapter.OnRadioButtonClickListener {
    lateinit var binding: ActivityShippingAddressBinding
    val TAG: String = ShippingAddressActivity::class.java.simpleName
    val addressList: ArrayList<Location> = ArrayList()
    lateinit var address: Location

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityShippingAddressBinding.inflate(layoutInflater)
        setContentView(binding.root)

        inits()
    }

    private fun inits() {

        binding.rvAddress.layoutManager = LinearLayoutManager(
            this,
            LinearLayoutManager.VERTICAL,
            false
        )
        binding.btnAddNewAddress.setOnClickListener {
            openPlacePicker()
        }
        getAddresses()
    }

    private fun refreshAddressAdapter(products: ArrayList<Location>) {
        val adapter = ShippingAddressAdapter(this, products, this)
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
            .setMapZoom(22.0f)  // Map Zoom Level. Default: 14.0
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
                GOOGLE_API_KEY
            ) //Activate GooglePlace Search Bar. Default is false/not activated. SearchBar is a chargeable feature by Google
            .onlyCoordinates(true)  //Get only Coordinates from Place Picker
            .hideLocationButton(false)   //Hide Location Button (Default: false)
            .disableMarkerAnimation(true)   //Disable Marker Animation (Default: false)
            .build(this)
        startActivityForResult(intent, Constants.PLACE_PICKER_REQUEST)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == Constants.PLACE_PICKER_REQUEST) {
            if (resultCode == Activity.RESULT_OK) {
                val addressData = data?.getParcelableExtra<AddressData>(Constants.ADDRESS_INTENT)
                showEditBottomsheet(addressData)

            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    fun showEditBottomsheet(addressData: AddressData?) {
        val sheetDialog = BottomSheetDialog(this, R.style.AppBottomSheetDialogTheme)
        val bottomSheetBinding: ItemAddLocationBottomsheetBinding =
            ItemAddLocationBottomsheetBinding.inflate(layoutInflater)
        sheetDialog.setContentView(bottomSheetBinding.root)
        val address = addressData?.longitude?.let {
            addressData.latitude.let { it1 ->
                Utils.getCoordinateName(
                    this,
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


    private fun getAddresses() {
        val reference = FirebaseDatabase.getInstance().reference
        val query: Query =
            reference.child("users").orderByChild("user_id")
                .equalTo(UserPrefManager(this).loadUser().user_id)
        query.addValueEventListener(object : ValueEventListener {
            @SuppressLint("SetTextI18n")
            override fun onDataChange(@NotNull snapshot: DataSnapshot) {
                if (snapshot.exists()) {

                    for (userSnapshot in snapshot.children) {
                        userSnapshot.ref.child("user_location").addValueEventListener(object  :ValueEventListener{
                            override fun onDataChange(snapshot: DataSnapshot) {
                                for (locationSnapshot in snapshot.children) {
                                    Logger.e(TAG,"locationSnapshot: "+locationSnapshot.toString())
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
            .equalTo(UserPrefManager(this).loadUser().user_id)
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


    override fun onRadioButtonClickListener(id: String) {
        toast(id)
    }
}