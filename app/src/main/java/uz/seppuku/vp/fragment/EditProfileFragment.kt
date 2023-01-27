package uz.seppuku.vp.fragment

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.database.*
import com.google.firebase.database.annotations.NotNull
import uz.seppuku.vp.databinding.FragmentEditProfileBinding
import uz.seppuku.vp.manager.UserPrefManager


class EditProfileFragment : Fragment(R.layout.fragment_edit_profile) {

    lateinit var binding: FragmentEditProfileBinding
    val TAG: String = EditProfileFragment::class.java.simpleName
    lateinit var mContext: Context
    var reference: DatabaseReference = FirebaseDatabase.getInstance().reference.child("users")
    lateinit var user_id: String
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentEditProfileBinding.bind(view)
        mContext = requireContext()


    }

    override fun onResume() {
        super.onResume()
        inits()
        user_id = UserPrefManager(mContext).loadUser()?.user_id.toString()
    }

    private fun inits() {
        displayUserDatas()

        setTextWatcher()

        binding.ivBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.btnApply.setOnClickListener {
            updateUserDatas()
        }

    }

    private fun updateUserDatas() {
        val query: Query = reference.orderByChild("user_id")
            .equalTo(user_id)

        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(@NotNull snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (ds in snapshot.children) {
                        ds.ref.child("user_first_name").setValue(binding.etFirstName.text.toString())
                        ds.ref.child("user_last_name").setValue(binding.etLastName.text.toString())
                        ds.ref.child("user_company_name").setValue(binding.etCompanyName.text.toString())
                        ds.ref.child("user_phone_number").setValue(binding.etPhoneNumber.text.toString())
                    }
                }
            }

            override fun onCancelled(@NotNull error: DatabaseError) {

            }
        })
    }

    private fun setTextWatcher() {
        binding.etFirstName.addTextChangedListener(textWatcher)
        binding.etLastName.addTextChangedListener(textWatcher)
        binding.etCompanyName.addTextChangedListener(textWatcher)
        binding.etPhoneNumber.addTextChangedListener(textWatcher)
    }

    private fun displayUserDatas() {
        val user = UserPrefManager(mContext).loadUser()
        binding.etFirstName.setText(user?.user_first_name.toString())
        binding.etLastName.setText(user?.user_last_name.toString())
        binding.etCompanyName.setText(user?.user_company_name.toString())
        binding.etPhoneNumber.setText(user?.user_phone_number.toString())
    }

    private val textWatcher = object : TextWatcher {
        var oldText: String = ""
        override fun afterTextChanged(s: Editable?) {

        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            oldText = s.toString()
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            if (s != oldText) {
                binding.btnApply.visibility = View.VISIBLE
            } else {
                binding.btnApply.visibility = View.GONE
            }
        }
    }

}