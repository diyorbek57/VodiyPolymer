package uz.seppuku.vp.fragment

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import uz.seppuku.vp.databinding.FragmentNoConnectionBinding
import uz.seppuku.vp.fragment.orders.OrderShippingAddressFragment

class NoConnectionFragment : Fragment() {

    lateinit var binding: FragmentNoConnectionBinding
    val TAG: String = OrderShippingAddressFragment::class.java.simpleName
    lateinit var mContext: Context
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentNoConnectionBinding.inflate(inflater, container, false)
        mContext = requireContext()
        inits()

        return binding.root
    }

    private fun inits() {

    }


}