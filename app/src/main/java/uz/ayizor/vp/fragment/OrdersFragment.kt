package uz.ayizor.vp.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import uz.ayizor.vp.R
import uz.ayizor.vp.adapter.OrdersViewPagerAdapter
import uz.ayizor.vp.databinding.FragmentOrdersBinding
import uz.ayizor.vp.fragment.orders.CompletedOrdersFragment
import uz.ayizor.vp.fragment.orders.OngoingOrdersFragment


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