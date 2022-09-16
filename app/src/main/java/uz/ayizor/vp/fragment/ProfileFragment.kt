package uz.ayizor.vp.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import uz.ayizor.vp.R
import uz.ayizor.vp.activity.AuthActivity
import uz.ayizor.vp.databinding.FragmentProfileBinding
import uz.ayizor.vp.databinding.ItemDoubleBottomsheetBinding
import uz.ayizor.vp.manager.UserPrefManager


class ProfileFragment : Fragment() {
    lateinit var binding: FragmentProfileBinding
    val TAG: String = CartFragment::class.java.simpleName
    lateinit var mContext: Context
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        mContext = requireContext()
        inits()
        return binding.root
    }

    private fun inits() {
        displayUserDatas()

        binding.rlLogout.setOnClickListener {
            showLogoutBottomSheet()
        }
        binding.rlEditProfile.setOnClickListener {
            val action = ProfileFragmentDirections.actionNavProfileToEditProfileFragment()
            findNavController().navigate(action)
        }
        binding.rlAddress.setOnClickListener {
            val action = ProfileFragmentDirections.actionNavProfileToShippingAddressFragment()
            findNavController().navigate(action)
        }
        binding.rlLanguage.setOnClickListener {
            val action = ProfileFragmentDirections.actionNavProfileToLanguageFragment()
            findNavController().navigate(action)
        }

    }

    @SuppressLint("SetTextI18n")
    private fun displayUserDatas() {
        val user = UserPrefManager(requireContext()).loadUser()
        binding.tvFullname.text = user?.user_first_name + " " + user?.user_last_name
        binding.tvPhoneNumber.text = user?.user_phone_number
    }

    @SuppressLint("SetTextI18n")
    private fun showLogoutBottomSheet() {
        val sheetDialog = BottomSheetDialog(mContext, R.style.AppBottomSheetDialogTheme)
        val bottomSheetBinding: ItemDoubleBottomsheetBinding =
            ItemDoubleBottomsheetBinding.inflate(layoutInflater)
        sheetDialog.setContentView(bottomSheetBinding.root)
        bottomSheetBinding.tvTitle.text = getString(R.string.logout)
        bottomSheetBinding.tvMessage.text = getString(R.string.are_you_sure_you_want_to_log_out)
        bottomSheetBinding.btnCancle.text = getString(R.string.cancle)
        bottomSheetBinding.btnConfirm.text = getString(R.string.yes_logout)
        bottomSheetBinding.btnCancle.setOnClickListener {
            sheetDialog.dismiss()
        }
        bottomSheetBinding.btnConfirm.setOnClickListener {
            UserPrefManager(mContext).nukeUser()
            val intent = Intent(mContext, AuthActivity::class.java)
            startActivity(intent)
            Firebase.auth.signOut()
            activity?.finish()
            sheetDialog.dismiss()
        }


        sheetDialog.show();
        sheetDialog.window?.attributes?.windowAnimations = R.style.DialogAnimaton;

    }

}