package com.ayizor.vodiypolymer.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.ayizor.vodiypolymer.databinding.FragmentProfileBinding
import com.ayizor.vodiypolymer.manager.UserPrefManager


class ProfileFragment : Fragment() {
    lateinit var binding: FragmentProfileBinding
    val TAG: String = CartFragment::class.java.simpleName
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        inits()
        return binding.root
    }

    private fun inits() {
        displayUserDatas()

    }

    @SuppressLint("SetTextI18n")
    private fun displayUserDatas() {
        val user = UserPrefManager(requireContext()).loadUser()
        binding.tvFullname.text = user.user_first_name + " " + user.user_last_name
        binding.tvPhoneNumber.text = user.user_phone_number
    }

}