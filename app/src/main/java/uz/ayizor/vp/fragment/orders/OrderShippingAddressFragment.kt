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
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.findNavController
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

class OrderShippingAddressFragment : Fragment(R.layout.fragment_order_shipping_address),
    ChooseShippingAddressAdapter.OnRadioButtonClickListener {
    lateinit var binding: FragmentOrderShippingAddressBinding
    lateinit var bottomSheetBinding: ItemAddLocationBottomsheetBinding
    val TAG: String = OrderShippingAddressFragment::class.java.simpleName
    val addressList: ArrayList<Location> = ArrayList()
    var addressData: AddressData? = null
    lateinit var address: Location
    lateinit var mContext: Context
    lateinit var reference: DatabaseReference
    lateinit var user_id: String

    companion object {
        val REQUEST_KEY = "selected_location"
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentOrderShippingAddressBinding.bind(view)
        mContext = requireContext()
        user_id = UserPrefManager(mContext).loadUser()?.user_id.toString()
        inits()
    }



    private fun inits() {
        reference = FirebaseDatabase.getInstance().reference.child("users_locations")
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
                getDefaultAddress()
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
        val query: Query = reference.orderByChild("location_user_id")
            .equalTo(user_id)
        query.addValueEventListener(object : ValueEventListener {
            @SuppressLint("SetTextI18n")
            override fun onDataChange(@NotNull snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    addressList.clear()
                    for (locationSnapshot in snapshot.children) {
                        address = locationSnapshot.getValue(Location::class.java)!!
                        address.location_name?.let { Log.e(TAG, it) }
                        addressList.add(address)
                    }

                    refreshAddressAdapter(addressList)

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

        reference.push().setValue(location)
    }

    private fun getDefaultAddress() {
        var defaultLocation: Location? = null
        val query: Query = reference.orderByChild("location_user_id")
            .equalTo(user_id)
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            @SuppressLint("SetTextI18n")
            override fun onDataChange(@NotNull snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (userSnapshot in snapshot.children) {
                        Logger.e(TAG, "getDefaultAddress : 1 :" + snapshot.exists().toString())

                        val location = userSnapshot.getValue(Location::class.java)
                        if (location?.location_isDefault == true && location.location_user_id == user_id) {
                            defaultLocation = location
                        }


                    }
                    if (defaultLocation != null) {
                        val id = defaultLocation!!.location_id.toString()
                        changeDefaultLocation(id)
                    } else {
                        createAddress(getCreatedAddress())
                    }
                }else{
                    createAddress(getCreatedAddress())
                }
            }

            override fun onCancelled(@NotNull error: DatabaseError) {
                Logger.e(TAG, "getDeafultAddress : 1 :" + error.message)
            }
        })
    }

    private fun changeDefaultLocation(locationId: String?) {

        val query: Query =
            reference.orderByChild("location_user_id").equalTo(user_id)
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            @SuppressLint("SetTextI18n")
            override fun onDataChange(@NotNull snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (userSnapshot in snapshot.children) {
                       val location = userSnapshot.getValue(Location::class.java)
                        if (location?.location_isDefault == true && location.location_user_id == user_id && location.location_id == locationId) {
                            userSnapshot.ref.child("location_isDefault").setValue(false)
                                .addOnSuccessListener {
                                    createAddress(getCreatedAddress())
                                }
                        }
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
                user_id,
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
                user_id,
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
        findNavController().previousBackStackEntry?.savedStateHandle?.set(REQUEST_KEY, id)
        findNavController().navigateUp()
    }
}