package uz.seppuku.vp.activity

import android.os.Bundle
import uz.seppuku.vp.databinding.ActivityAuthBinding


class AuthActivity : BaseActivity() {

    lateinit var binding: ActivityAuthBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)




    }


//    private fun setupNavigation() {
//        val navHostFragment = supportFragmentManager.findFragmentById(binding.navHostFragment.id) as NavHostFragment
//        val navController = navHostFragment.navController
//    }
}