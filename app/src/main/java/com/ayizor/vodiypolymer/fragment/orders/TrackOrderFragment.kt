package com.ayizor.vodiypolymer.fragment.orders

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ayizor.vodiypolymer.R
import com.ayizor.vodiypolymer.databinding.FragmentCartBinding
import com.ayizor.vodiypolymer.databinding.FragmentTrackOrderBinding
import com.ayizor.vodiypolymer.fragment.CartFragment
import com.ayizor.vodiypolymer.model.Order


class TrackOrderFragment : Fragment() {

    lateinit var binding: FragmentTrackOrderBinding
    val TAG: String = TrackOrderFragment::class.java.simpleName
    lateinit var product: Order
    val productsList: ArrayList<Order> = ArrayList()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTrackOrderBinding.inflate(inflater, container, false)
        inits()
        return binding.root
    }

    private fun inits() {


    }

}