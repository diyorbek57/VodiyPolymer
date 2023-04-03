package uz.seppuku.vp.fragment

import android.annotation.SuppressLint
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
import dagger.hilt.android.AndroidEntryPoint
import uz.seppuku.afeme.helper.CustomSpannable
import uz.seppuku.vp.R
import uz.seppuku.vp.adapter.ViewPagerAdapter
import uz.seppuku.vp.databinding.FragmentDetailsBinding
import uz.seppuku.vp.model.Image
import uz.seppuku.vp.model.Product
import uz.seppuku.vp.viewmodel.HomeViewModel

@AndroidEntryPoint
class DetailsFragment : Fragment(R.layout.fragment_details) {


    private var _binding: FragmentDetailsBinding? = null
    private val binding get() = _binding!!
    val TAG: String = "DetailsFragment"
    private val viewModel by viewModels<HomeViewModel>()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentDetailsBinding.bind(view)

        inits()
    }

    override fun onResume() {
        super.onResume()

        // viewModel.getSingleProduct()

    }

    private fun inits() {
        initUI()
        setupQuantityStepper()

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
                tvPrice.text = "$product_price So'm"
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
                    makeTextViewResizable(
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

    private fun makeTextViewResizable(
        tv: TextView,
        maxLine: Int,
        expandText: String,
        viewMore: Boolean
    ) {
        if (tv.tag == null) {
            tv.tag = tv.text
        }
        val vto: ViewTreeObserver = tv.viewTreeObserver
        vto.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                val text: String
                val lineEndIndex: Int
                val obs: ViewTreeObserver = tv.viewTreeObserver
                obs.removeOnGlobalLayoutListener(this)
                if (maxLine == 0) {
                    lineEndIndex = tv.layout.getLineEnd(0)
                    text = tv.text.subSequence(0, lineEndIndex - expandText.length + 1)
                        .toString() + " " + expandText
                } else if (maxLine > 0 && tv.lineCount >= maxLine) {
                    lineEndIndex = tv.layout.getLineEnd(maxLine - 1)
                    text = tv.text.subSequence(0, lineEndIndex - expandText.length + 1)
                        .toString() + " " + expandText
                } else {
                    lineEndIndex = tv.layout.getLineEnd(tv.layout.lineCount - 1)
                    text = tv.text.subSequence(0, lineEndIndex).toString() + " " + expandText
                }
                tv.text = text
                tv.movementMethod = LinkMovementMethod.getInstance()
                tv.setText(
                    addClickablePartTextViewResizable(
                        SpannableString(tv.text.toString()), tv, lineEndIndex, expandText,
                        viewMore
                    ), TextView.BufferType.SPANNABLE
                )
            }
        })
    }


    private fun addClickablePartTextViewResizable(
        strSpanned: Spanned,
        tv: TextView,
        maxLine: Int,
        spanableText: String,
        viewMore: Boolean
    ): SpannableStringBuilder? {
        val str = strSpanned.toString()
        val ssb = SpannableStringBuilder(strSpanned)

        if (str.contains(spanableText)) {
            ssb.setSpan(object : CustomSpannable(false) {
                override fun onClick(widget: View) {
                    super.onClick(widget)
                    tv.layoutParams = tv.layoutParams
                    tv.setText(tv.tag.toString(), TextView.BufferType.SPANNABLE)
                    tv.invalidate()
                    if (viewMore) {
                        makeTextViewResizable(tv, -1, getString(R.string.view_less), false)
                    } else {
                        makeTextViewResizable(tv, 3, getString(R.string.view_more), true)
                    }
                }
            }, str.indexOf(spanableText), str.indexOf(spanableText) + spanableText.length, 0)
        }
        return ssb
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}