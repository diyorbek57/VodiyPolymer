package uz.seppuku.vp.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.rajat.pdfviewer.PdfViewerActivity
import uz.seppuku.vp.R
import uz.seppuku.vp.activity.AuthActivity
import uz.seppuku.vp.databinding.FragmentProfileBinding
import uz.seppuku.vp.databinding.ItemDoubleBottomsheetBinding
import uz.seppuku.vp.manager.UserPrefManager


class ProfileFragment : Fragment(R.layout.fragment_profile) {
    lateinit var binding: FragmentProfileBinding
    val TAG: String = ProfileFragment::class.java.simpleName
    lateinit var mContext: Context


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentProfileBinding.bind(view)
        mContext = requireContext()
        inits()
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
        binding.rlNotifications.setOnClickListener {
            val action = ProfileFragmentDirections.actionNavProfileToNotificationSettingFragment()
            findNavController().navigate(action)
        }
        binding.rlPrivacy.setOnClickListener {
            startActivity(

                // Use 'launchPdfFromPath' if you want to use assets file (enable "fromAssets" flag) / internal directory

                PdfViewerActivity.Companion.launchPdfFromUrl(           //PdfViewerActivity.Companion.launchPdfFromUrl(..   :: incase of JAVA
                    context,
                    "https://firebasestorage.googleapis.com/v0/b/vodiy-polymer.appspot.com/o/Privacy%20Policy%2FPrivacy%20Policy.pdf?alt=media&token=1b287602-6d41-4356-bdc0-dd12ebbed075",                                // PDF URL in String format
                    getString(R.string.privacy_policy),                        // PDF Name/Title in String format
                    "",                  // If nothing specific, Put "" it will save to Downloads
                    enableDownload = false,                 // This param is true by defualt.

                )
            )
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