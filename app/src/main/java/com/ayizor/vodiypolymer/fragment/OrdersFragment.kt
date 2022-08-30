package com.ayizor.vodiypolymer.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.ayizor.vodiypolymer.R
import com.ayizor.vodiypolymer.adapter.OrdersViewPagerAdapter
import com.ayizor.vodiypolymer.databinding.FragmentCartBinding
import com.ayizor.vodiypolymer.databinding.FragmentOrdersBinding
import com.ayizor.vodiypolymer.fragment.orders.CompletedOrdersFragment
import com.ayizor.vodiypolymer.fragment.orders.OngoingOrdersFragment
import com.google.android.material.tabs.TabLayout


class OrdersFragment : Fragment() {

    lateinit var binding: FragmentOrdersBinding
    lateinit var viewPagerAdapter: OrdersViewPagerAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentOrdersBinding.inflate(inflater, container, false)
        inits()
        return binding.root
    }

    private fun inits() {
        setupViewPager()
    }

    private fun setupViewPager() {
        viewPagerAdapter = OrdersViewPagerAdapter(childFragmentManager)
        viewPagerAdapter.addFragment(OngoingOrdersFragment(), getString(R.string.ongoing))
        viewPagerAdapter.addFragment(CompletedOrdersFragment(), getString(R.string.completed))
        binding.vpFavorites.adapter = viewPagerAdapter
        binding.tlFavorites.setupWithViewPager(binding.vpFavorites)


    }

}