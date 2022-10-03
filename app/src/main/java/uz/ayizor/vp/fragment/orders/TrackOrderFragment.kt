package uz.ayizor.vp.fragment.orders

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import uz.ayizor.vp.R
import uz.ayizor.vp.databinding.FragmentTrackOrderBinding
import uz.ayizor.vp.model.Cart


class TrackOrderFragment : Fragment() {

    lateinit var binding: FragmentTrackOrderBinding
    val TAG: String = TrackOrderFragment::class.java.simpleName
    private val args: TrackOrderFragmentArgs by navArgs()
    lateinit var product: Cart
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTrackOrderBinding.inflate(inflater, container, false)
        inits()
        return binding.root
    }

    private fun inits() {
        val product = args.order
        val step = args.step
        displayDatas(product, step)

        binding.ivBack.setOnClickListener {
            activity?.onBackPressed()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun displayDatas(product: Cart, step: Int) {
        if (step == 1) {
            binding.tvTrackStatus.text = getString(R.string.order_confirmed)
            binding.ivTrackStatus.setImageResource(R.drawable.img_track_1)
        } else if (step == 2) {
            binding.tvTrackStatus.text = getString(R.string.order_shipped)
            binding.ivTrackStatus.setImageResource(R.drawable.img_track_2)
        } else {
            binding.tvTrackStatus.text = getString(R.string.order_delivered)
            binding.ivTrackStatus.setImageResource(R.drawable.img_track_3)
        }
        //product datas
        binding.tvPrice.text = product.cart_product_total_price
        binding.tvQuantity.text = product.cart_product_total_quantity
        binding.tvTitle.text = product.cart_product?.product_name
        Glide.with(requireContext()).load(product.cart_product?.product_image?.get(0)?.image_url)
            .into(binding.ivImage)
    }

}