package uz.ayizor.vp.authentication

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import uz.ayizor.vp.R
import uz.ayizor.vp.databinding.FragmentSignUpBinding
import uz.ayizor.vp.utils.Utils


class SignUpFragment : Fragment() {
    lateinit var binding: FragmentSignUpBinding
    val TAG: String = SignUpFragment::class.java.simpleName
    lateinit var user_company_name: String
    lateinit var user_first_name: String
    lateinit var user_last_name: String
    lateinit var user_phone_number: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSignUpBinding.inflate(inflater, container, false)

        inits()
        return binding.root
    }

    private fun inits() {


        binding.btnSignup.setOnClickListener {
            if (Utils.validEditText(
                    binding.tilNumber.editText,
                    getString(R.string.please_enter_your_phone_number)
                )&&Utils.validEditText(
                    binding.tilFirstName.editText,
                    getString(R.string.please_enter_your_first_name)
                )&&Utils.validEditText(
                    binding.tilLastName.editText,
                    getString(R.string.please_enter_your_first_name)
                )&&Utils.validEditText(
                    binding.tilCompanyName.editText,
                    getString(R.string.please_enter_your_first_name)
                )

            ) {
                user_phone_number = binding.tilNumber.editText?.text.toString()
                user_first_name = binding.tilFirstName.editText?.text.toString()
                user_last_name = binding.tilLastName.editText?.text.toString()
                user_company_name = binding.tilCompanyName.editText?.text.toString()
                callCodeConfirmFragment()
            }
        }
        binding.tvSignin.setOnClickListener {
            val action = SignUpFragmentDirections.actionSignUpFragmentToSignInFragment()
            findNavController().navigate(action)
        }
    }


    fun callCodeConfirmFragment() {
        val action = SignUpFragmentDirections.actionSignUpFragmentToCodeConfirmFragment()
            .setNumber(user_phone_number)
            .setFirstName(user_first_name)
            .setLastName(user_last_name)
            .setCompanyName(user_company_name)
            .setAuthType("signup")
        findNavController().navigate(action)
    }


}

