package uz.seppuku.vp.fragment

import android.annotation.SuppressLint
import android.graphics.Paint
import android.os.Bundle
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.view.View
import android.view.ViewTreeObserver
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint
import uz.seppuku.afeme.helper.CustomSpannable
import uz.seppuku.vp.R
import uz.seppuku.vp.adapter.ViewPagerAdapter
import uz.seppuku.vp.databinding.FragmentDetailsBinding
import uz.seppuku.vp.helper.ResizableTextView
import uz.seppuku.vp.model.Image
import uz.seppuku.vp.model.Product
import uz.seppuku.vp.utils.Logger
import uz.seppuku.vp.utils.Resource
import uz.seppuku.vp.viewmodel.HomeViewModel

@AndroidEntryPoint
class DetailsFragment : Fragment(R.layout.fragment_details) {


    private var _binding: FragmentDetailsBinding? = null
    private val binding get() = _binding!!
    val TAG: String = "DetailsFragment"
    private val viewModel by viewModels<HomeViewModel>()

    private val args: DetailsFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentDetailsBinding.bind(view)

        inits()
    }

    override fun onResume() {
        super.onResume()
        viewModel.getSingleProduct(args.productId)

    }

    private fun inits() {
        initUI()
        setupQuantityStepper()
        observer()
    }

    private fun observer() {
        viewModel.product.observe(viewLifecycleOwner) {
            when (it.status) {
                Resource.Status.SUCCESS -> {
                    it.data?.let { it1 -> displayProduct(it1) }
                    //  Utils.hideLoading(binding.progressBar)
                }

                Resource.Status.ERROR -> {
                    it.message?.let { it1 -> Logger.e(TAG, it1) }
                    //  Utils.hideLoading(binding.progressBar)
                }

                Resource.Status.LOADING -> {
                    //  Utils.showLoading(binding.progressBar)
                }
            }
        }
    }

    private fun initUI() {


        binding.apply {
            btnAddToCart.setOnClickListener {

            }
        }


    }


    private fun setupQuantityStepper() {


    }


    @SuppressLint("SetTextI18n")
    private fun displayProduct(product: Product) {
        product.apply {
            binding.apply {


                //price
                if (product_discount.isNullOrEmpty()) {
                    llDiscount?.visibility = View.GONE
                    tvDicountPrice?.text = "$product_price So'm"
                } else {

                    tvPrice.paintFlags  = tvPrice.paintFlags.or(Paint.STRIKE_THRU_TEXT_FLAG)
                    tvDicountPrice?.text = "$product_discount_price So'm"
                    tvPrice.text = "$product_price"
                    tvDiscount?.text = "-$product_discount %"
                }

                tvTotalPrice.text = "$product_price So'm"
                //image
                product_image?.let { setupViewPager(it) }
                //rating
                tvRating.text = product_rating.toString()
                //solds
                tvSold.text = product_sold + " " + getString(R.string.sold)
                //title
                tvTitle.text = product_name
                //description
                tvDescription.text = product_description
                if (product_description?.length!! > 70)
                    ResizableTextView(requireContext()).makeTextViewResizable(
                        binding.tvDescription,
                        3,
                        getString(R.string.view_more),
                        true
                    )
            }
        }
    }

    private fun setupViewPager(images: ArrayList<Image>) {

        val viewPagerAdapter = ViewPagerAdapter(images, requireContext())

        binding.apply {
            viewpager.currentItem = 0
            viewpager.offscreenPageLimit = 3
            viewpager.adapter = viewPagerAdapter
            viewpager.clipToPadding = false
            viewpager.clipChildren = false
            viewpager.getChildAt(0).overScrollMode = View.OVER_SCROLL_NEVER
            binding.dotsIndicator.attachTo(binding.viewpager)
        }


    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}