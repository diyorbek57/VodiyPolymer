package uz.seppuku.vp.authentication

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.database.*
import com.google.firebase.database.annotations.NotNull
import uz.seppuku.vp.activity.MainActivity
import uz.seppuku.vp.databinding.FragmentSignInBinding
import uz.seppuku.vp.manager.UserPrefManager


class SignInFragment : Fragment() {


    lateinit var binding: FragmentSignInBinding
    val TAG: String = SignInFragment::class.java.simpleName

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSignInBinding.inflate(inflater, container, false)

        try {
            if (!UserPrefManager(requireContext()).loadUser()?.user_id.isNullOrEmpty()) {
                val intent = Intent(requireContext(), MainActivity::class.java)
                startActivity(intent)
                activity?.finish()
            } else {
                inits()
            }
        } catch (e: Exception) {
            uz.seppuku.vp.utils.Logger.e(TAG, e.message.toString())
            inits()
        }


        return binding.root
    }

    private fun inits() {
        binding.tvSignup.setOnClickListener {
            val action = SignInFragmentDirections.actionSignInFragmentToSignUpFragment()
            findNavController().navigate(action)
        }

        binding.btnSignin.setOnClickListener {
            uz.seppuku.vp.utils.Logger.e(TAG, "SignIn Button clicked")
            binding.btnSignin.startAnimation()
            checkUser()
        }
    }


    private fun callCodeConfirmFragment(phoneNumber: String) {

        val action = SignInFragmentDirections.actionSignInFragmentToCodeConfirmFragment()
            .setNumber(phoneNumber).setAuthType("signin")
        findNavController().navigate(action)
    }

    private fun checkUser() {

        val reference = FirebaseDatabase.getInstance().getReference("users")
        val query: Query = reference.orderByChild("user_phone_number")
            .equalTo(binding.tilNumber.editText?.text.toString())
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(@NotNull snapshot: DataSnapshot) {
                uz.seppuku.vp.utils.Logger.e(TAG, snapshot.exists().toString())
                if (snapshot.exists()) {
                    binding.btnSignin.dispose()
                    callCodeConfirmFragment(
                        binding.tilNumber.editText?.text.toString()
                    )
                } else {
                    binding.btnSignin.dispose()
                    Toast.makeText(
                        requireContext(),
                        "Raqam ro'yxatdan o'tmagan !",
                        Toast.LENGTH_SHORT
                    ).show()

                }
            }

            override fun onCancelled(@NotNull error: DatabaseError) {
                uz.seppuku.vp.utils.Logger.e(TAG, error.message)
            }

        })
    }


}