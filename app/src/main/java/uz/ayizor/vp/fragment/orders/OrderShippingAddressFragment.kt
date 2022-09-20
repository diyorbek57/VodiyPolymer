package uz.ayizor.vp.fragment.orders

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
import com.sucho.placepicker.Constants.GOOGLE_API_KEY
import com.sucho.placepicker.MapType
import com.sucho.placepicker.PlacePicker
import uz.ayizor.afeme.utils.Extensions.toast
import uz.ayizor.vp.R
import uz.ayizor.vp.adapter.ChooseShippingAddressAdapter
import uz.ayizor.vp.databinding.FragmentOrderShippingAddressBinding
import uz.ayizor.vp.databinding.ItemAddLocationBottomsheetBinding
import uz.ayizor.vp.manager.UserPrefManager
import uz.ayizor.vp.model.Location
import uz.ayizor.vp.utils.Logger
import uz.ayizor.vp.utils.Utils

class OrderShippingAddressFragment : Fragment(),
    ChooseShippingAddressAdapter.OnRadioButtonClickListener {
    lateinit var binding: FragmentOrderShippingAddressBinding
    lateinit var bottomSheetBinding: ItemAddLocationBottomsheetBinding
    val TAG: String = OrderShippingAddressFragment::class.java.simpleName
    val addressList: ArrayList<Location> = ArrayList()
    var addressData: AddressData? = null
    lateinit var address: Location
    lateinit var mContext: Context
    lateinit var reference: DatabaseReference
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentOrderShippingAddressBinding.inflate(inflater, container, false)
        mContext = requireContext()
        inits()

        return binding.root
    }


    private fun inits() {
        reference = FirebaseDatabase.getInstance().reference.child("users")
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
        val adapter = ChooseShippingAddressAdapter(mContext, products, this)
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
                GOOGLE_API_KEY
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
                addressData = data?.getParcelableExtra<AddressData>(Constants.ADDRESS_INTENT)
                showEditBottomsheet(addressData)

            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun showEditBottomsheet(addressData: AddressData?) {
        val sheetDialog = BottomSheetDialog(mContext, R.style.AppBottomSheetDialogTheme)
        bottomSheetBinding =
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

            if (bottomSheetBinding.isDefault.isChecked) {
                getDeafultAddress()
                sheetDialog.dismiss()
            } else {
                createAddress(getCreatedAddress())
                sheetDialog.dismiss()
            }


        }

        sheetDialog.show();
        sheetDialog.window?.attributes?.windowAnimations = R.style.DialogAnimaton;
    }


    private fun getAddresses() {
        val query: Query = reference.orderByChild("user_id")
            .equalTo(UserPrefManager(mContext).loadUser()?.user_id)
        query.addValueEventListener(object : ValueEventListener {
            @SuppressLint("SetTextI18n")
            override fun onDataChange(@NotNull snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (userSnapshot in snapshot.children) {
                        userSnapshot.ref.child("user_location")
                            .addValueEventListener(object : ValueEventListener {
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    if (snapshot.exists()) {
                                        addressList.clear()
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
        val query: Query = reference.orderByChild("user_id")
            .equalTo(UserPrefManager(mContext).loadUser()?.user_id)
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            @SuppressLint("SetTextI18n")
            override fun onDataChange(@NotNull snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (userSnapshot in snapshot.children) {
                        Logger.e(TAG, "1:" + location.toString())

                        userSnapshot.ref.child("user_location").push().setValue(location)
                    }
                }
            }

            override fun onCancelled(@NotNull error: DatabaseError) {}
        })
    }

    private fun getDeafultAddress() {
        val query: Query = reference.orderByChild("user_id")
            .equalTo(UserPrefManager(mContext).loadUser()?.user_id)
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            @SuppressLint("SetTextI18n")
            override fun onDataChange(@NotNull snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (userSnapshot in snapshot.children) {
                        Logger.e(TAG, "getDeafultAddress : 1 :"+  snapshot.exists().toString())
                        userSnapshot.ref.child("user_location").orderByChild("location_isDefault").equalTo(true)
                            .addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    if (snapshot.exists()) {
                                    for (locationsSnapshot in snapshot.children) {
                                        Logger.e(TAG, "getDeafultAddress : 2 :" + locationsSnapshot.exists().toString())


                                            val id = locationsSnapshot.child("location_id").getValue(String::class.java)
                                            changeDefaultLocation(id)
                                            Logger.e(TAG, "getDeafultAddress : 2 :" + id.toString())



                                    }
                                    } else {
                                        createAddress(getCreatedAddress())
                                    }
                                }

                                override fun onCancelled(error: DatabaseError) {
                                    Logger.e(TAG, "getDeafultAddress : 2 : error message:" + error.message)
                                    Logger.e(TAG, "getDeafultAddress : 2 : error code" + error.code)
                                    Logger.e(TAG, "getDeafultAddress : 2 : error code" + error.toException())
                                    Logger.e(TAG, "getDeafultAddress : 2 : error code" + error.toException())
                                }

                            })
                    }
                }
            }

            override fun onCancelled(@NotNull error: DatabaseError) {
                Logger.e(TAG, "getDeafultAddress : 1 :" + error.message)
            }
        })
    }

    private fun changeDefaultLocation(locationId: String?) {
        val query: Query =
            reference.orderByChild("user_id").equalTo(UserPrefManager(mContext).loadUser()?.user_id)
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            @SuppressLint("SetTextI18n")
            override fun onDataChange(@NotNull snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (userSnapshot in snapshot.children) {
                        userSnapshot.ref.child("user_location").orderByChild("location_id")
                            .equalTo(locationId)
                            .addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    for (locationsSnapshot in snapshot.children) {
                                        if (locationsSnapshot.exists()) {
                                            locationsSnapshot.ref.child("location_isDefault")
                                                .setValue(false).addOnSuccessListener {
                                                    createAddress(getCreatedAddress())
                                                }
                                        }


                                    }
                                }

                                override fun onCancelled(error: DatabaseError) {

                                }

                            })

                    }
                }
            }

            override fun onCancelled(@NotNull error: DatabaseError) {}
        })
    }

    private fun getCreatedAddress(): Location {
        if (bottomSheetBinding.etSearch.text.isNullOrEmpty()) {
            return Location(
                Utils.getUUID(),
                "Address",
                addressData?.latitude.toString(),
                addressData?.longitude.toString(),
                bottomSheetBinding.isDefault.isChecked,
                Utils.getCurrentTime(),
                Utils.getCurrentTime()
            )


        } else {
            return Location(
                Utils.getUUID(),
                bottomSheetBinding.etSearch.text.toString(),
                addressData?.latitude.toString(),
                addressData?.longitude.toString(),
                bottomSheetBinding.isDefault.isChecked,
                Utils.getCurrentTime(),
                Utils.getCurrentTime()
            )
        }
    }


    override fun onRadioButtonClickListener(id: String) {
        toast(id)
    }
}