package uz.seppuku.vp.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.firebase.FirebaseApp
import com.google.firebase.appcheck.FirebaseAppCheck
import com.google.firebase.appcheck.safetynet.SafetyNetAppCheckProviderFactory
import com.google.firebase.database.*
import com.google.firebase.database.annotations.NotNull
import kotlinx.coroutines.flow.collectLatest
import uz.seppuku.afeme.utils.Extensions.toast
import uz.seppuku.vp.databinding.ActivityMainBinding
import uz.seppuku.vp.helper.ConnectivityObserver
import uz.seppuku.vp.helper.NetworkConnectivityObserver
import uz.seppuku.vp.manager.UserPrefManager
import uz.seppuku.vp.model.Location
import uz.seppuku.vp.model.User
import uz.seppuku.vp.R
import uz.seppuku.vp.utils.Logger


class MainActivity : BaseActivity() {

    lateinit var binding: ActivityMainBinding
    val TAG: String = MainActivity::class.java.simpleName
    lateinit var user: User
    val addressList: ArrayList<Location> = ArrayList()
    lateinit var address: Location
    private lateinit var connectivityObserver: ConnectivityObserver
    val reference = FirebaseDatabase.getInstance().reference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        connectivityObserver = NetworkConnectivityObserver(applicationContext)

        lifecycleScope.launchWhenCreated {
            connectivityObserver.observe().collectLatest {
                if (it.toString().contentEquals("Available")) {
                    toast("Available")
                } else if (it.toString().contentEquals("Unavailable")) {
                    toast("Unavailable")
                } else if (it.toString().contentEquals("Losing")) {
                    toast("Losing")
                } else {
                    toast("Lost ")
                }
            }
        }

        setupNavigation()
        inits()
    }

    private fun inits() {
        val extras = intent.extras
        if (extras != null) {
            val phone_number = extras.getString("user_phone_number")
            try {
                if (phone_number != null) {
                    getUserDatas(phone_number)
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

        Logger.e(TAG, UserPrefManager(this).loadUser().toString())


    }

    private fun getUserDatas(phone_number: String) {

        val userQuery: Query =
            reference.child("users").orderByChild("user_phone_number")
                .equalTo(phone_number)
        userQuery.addValueEventListener(object : ValueEventListener {
            @SuppressLint("SetTextI18n")
            override fun onDataChange(@NotNull snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (userSnapshot in snapshot.children) {

                        user = userSnapshot.getValue(User::class.java)!!

                    }
                    if (!user.user_id.isNullOrEmpty()) {
                        UserPrefManager(this@MainActivity).storeUser(user)
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
        binding.bottomNavView.visibility = View.VISIBLE
        binding.bottomNavView.clearAnimation();
        binding.bottomNavView.animate().translationY(0F).duration = 300;

    }

    private fun hideBottomNav() {
        binding.bottomNavView.visibility = View.GONE
//        binding.bottomNavView.clearAnimation();
//        binding.bottomNavView.animate().translationY(binding.bottomNavView.height.toFloat()).duration =
//            300;

    }

}