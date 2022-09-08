package uz.ayizor.vp.fragment

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import uz.ayizor.vp.R
import uz.ayizor.vp.databinding.FragmentCompletedBinding
import uz.ayizor.vp.databinding.FragmentEditProfileBinding
import uz.ayizor.vp.fragment.orders.OngoingOrdersFragment
import uz.ayizor.vp.model.Order


class EditProfileFragment : Fragment() {

    lateinit var binding: FragmentEditProfileBinding
    val TAG: String = EditProfileFragment::class.java.simpleName
    lateinit var mContext: Context
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentEditProfileBinding.inflate(inflater, container, false)
        mContext= requireContext()
        inits()

        return binding.root
    }

    private fun inits() {


    }

}