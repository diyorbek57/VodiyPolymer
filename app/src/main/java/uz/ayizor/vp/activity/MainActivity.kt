package uz.ayizor.vp.activity

import android.annotation.SuppressLint
import android.nfc.Tag
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import uz.ayizor.vp.utils.Logger
import uz.ayizor.vp.databinding.ActivityMainBinding
import uz.ayizor.vp.manager.UserPrefManager
import uz.ayizor.vp.model.Location
import uz.ayizor.vp.model.User
import com.google.firebase.FirebaseApp
import com.google.firebase.appcheck.FirebaseAppCheck
import com.google.firebase.appcheck.safetynet.SafetyNetAppCheckProviderFactory
import com.google.firebase.database.*
import com.google.firebase.database.annotations.NotNull
import uz.ayizor.vp.R


class MainActivity : BaseActivity() {

    lateinit var binding: ActivityMainBinding
    val TAG: String = MainActivity::class.java.simpleName
    lateinit var user: User
    val addressList: ArrayList<Location> = ArrayList()
    lateinit var address: Location
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupNavigation()
        inits()
    }

    private fun inits() {
        val extras = intent.extras
        if (extras != null) {
            val user_id = extras.getString("user_phone_number")
            try {
                if (user_id != null) {
                    getUserDatas(user_id)
                }
            } catch (e: Exception) {
                Logger.e(TAG, e.stackTraceToString())
            }
        }
        FirebaseApp.initializeApp(this)
        val firebaseAppCheck = FirebaseAppCheck.getInstance()
        firebaseAppCheck.installAppCheckProviderFactory(
            SafetyNetAppCheckProviderFactory.getInstance()
        )

        Logger.e(TAG,UserPrefManager(this).loadUser().toString())


    }

    private fun getUserDatas(id: String) {

        val reference = FirebaseDatabase.getInstance().reference
        val query: Query =
            reference.child("users").orderByChild("user_phone_number")
                .equalTo(id)
        query.addValueEventListener(object : ValueEventListener {
            @SuppressLint("SetTextI18n")
            override fun onDataChange(@NotNull snapshot: DataSnapshot) {
                if (snapshot.exists()) {


                    for (userSnapshot in snapshot.children) {
                        val user_id: String? =
                            userSnapshot.child("user_id").getValue(String::class.java)
                        Logger.e(TAG, user_id.toString())
                        val user_first_name: String? =
                            userSnapshot.child("user_first_name").getValue(String::class.java)
                        val user_last_name: String? =
                            userSnapshot.child("user_last_name").getValue(String::class.java)
                        val user_company_name: String? =
                            userSnapshot.child("user_company_name").getValue(String::class.java)
                        val user_phone_number: String? =
                            userSnapshot.child("user_phone_number").getValue(String::class.java)
                        val user_device_id: String? =
                            userSnapshot.child("user_device_id").getValue(String::class.java)
                        val user_device_token: String? =
                            userSnapshot.child("user_device_token").getValue(String::class.java)
                        val user_created_at: String? =
                            userSnapshot.child("user_created_at").getValue(String::class.java)
                        val user_updated_at: String? =
                            userSnapshot.child("user_updated_at").getValue(String::class.java)
                        userSnapshot.ref.child("user_location")
                            .addValueEventListener(object : ValueEventListener {
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    if (snapshot.exists()) {


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
                                    }

                                    user = User(
                                        user_id,
                                        user_first_name,
                                        user_last_name,
                                        user_company_name,
                                        user_phone_number,
                                        user_device_id,
                                        user_device_token,
                                        addressList,
                                        null,
                                        user_created_at,
                                        user_updated_at
                                    )
                                    if (!user.user_id.isNullOrEmpty()) {
                                        UserPrefManager(this@MainActivity).storeUser(user)
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


    private fun setupNavigation() {
        val navHostFragment =
            supportFragmentManager.findFragmentById(binding.navHostFragment.id) as NavHostFragment
        val navController = navHostFragment.navController
        binding.bottomNavView.setupWithNavController(navController)
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.nav_home -> showBottomNav()
                R.id.nav_cart -> showBottomNav()
                R.id.nav_orders -> showBottomNav()
                R.id.nav_profile -> showBottomNav()
                else -> hideBottomNav()
            }
        }
    }

    private fun showBottomNav() {
        binding.bottomNavView.visibility=View.VISIBLE
        binding.bottomNavView.clearAnimation();
        binding.bottomNavView.animate().translationY(0F).duration = 300;

    }

    private fun hideBottomNav() {
        binding.bottomNavView.visibility=View.GONE
//        binding.bottomNavView.clearAnimation();
//        binding.bottomNavView.animate().translationY(binding.bottomNavView.height.toFloat()).duration =
//            300;

    }

}