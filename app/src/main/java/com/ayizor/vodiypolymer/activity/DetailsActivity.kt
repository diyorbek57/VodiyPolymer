package com.ayizor.vodiypolymer.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.util.Log
import android.view.View
import android.view.ViewTreeObserver
import android.widget.TextView
import androidx.viewpager2.widget.ViewPager2
import com.ayizor.afeme.helper.CustomSpannable
import com.ayizor.vodiypolymer.R
import com.ayizor.vodiypolymer.adapter.ViewPagerAdapter
import com.ayizor.vodiypolymer.databinding.ActivityDetailsBinding
import com.ayizor.vodiypolymer.manager.UserPrefManager
import com.ayizor.vodiypolymer.model.Image
import com.ayizor.vodiypolymer.model.Order
import com.ayizor.vodiypolymer.model.Product
import com.ayizor.vodiypolymer.utils.Utils
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.mcdev.quantitizerlibrary.AnimationStyle
import com.mcdev.quantitizerlibrary.QuantitizerListener
import java.math.BigDecimal

class DetailsActivity : BaseActivity() {
    lateinit var binding: ActivityDetailsBinding
    val TAG: String = DetailsActivity::class.java.simpleName

    //variables
    lateinit var id: String
    private var viewPager: ViewPager2? = null
    lateinit var viewPagerAdapter: ViewPagerAdapter
    private lateinit var database: DatabaseReference
    lateinit var product: Product
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        inits()
    }

    private fun inits() {
        database = Firebase.database.reference
        val extras = intent.extras
        if (extras != null) {
            id = extras.getString("id").toString()
            Log.e(TAG, "product id: " + id)
            getProduct(id)
        }
        setupQuantityStepper()

        binding.btnAddToCart.setOnClickListener {
            addToCart()
        }
    }

    private fun addToCart() {
        val totalPrice= product.product_price?.toInt()
            ?.let { binding.quantityStepper.value.times(it) }
        val product = Order(
            product.product_id,
            UserPrefManager(context).loadUser().user_id,
            product.product_name,
            product.product_description,
            totalPrice.toString(),
            binding.quantityStepper.value.toString(),
            product.product_discount,
            product.product_image, false,
            0,
            Utils.getCurrentTime(),
            Utils.getCurrentTime()
        )

       database.child("carts").push().setValue(product)
           .addOnSuccessListener {
               Utils.showSuccessToast(this, "Product Added","Product successfully added to cart")
           }
           .addOnFailureListener {
               Utils.showErrorToast(this, "Product Not Added","Error adding product to cart")
           }

    }

    private fun setupQuantityStepper() {
        binding.quantityStepper.textAnimationStyle = AnimationStyle.SWING
        binding.quantityStepper.isReadOnly = false
        binding.quantityStepper.setValueBackgroundColor(R.color.gray)
        binding.quantityStepper.setMinusIconColor(R.color.very_dark_gray_mostly_black)
        binding.quantityStepper.setPlusIconColor(R.color.very_dark_gray_mostly_black)
        binding.quantityStepper.setPlusIconBackgroundColor(R.color.gray)
        binding.quantityStepper.setMinusIconBackgroundColor(R.color.gray)
        binding.quantityStepper.minValue = 1
        binding.quantityStepper.maxValue = 255


    }

    private fun getProduct(id: String) {
        val productsList: java.util.ArrayList<Product> = java.util.ArrayList()
        val productListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (postSnapshot in dataSnapshot.children) {

                    val product = postSnapshot.getValue(Product::class.java)
                    if (product != null) {
                        Log.e(TAG, product.toString())
                        productsList.add(product)
                    }
                    // here you can access to name property like university.name
                }

                if (productsList != null) {
                    product = productsList[0]
                    displayProduct(productsList[0])
                }
                // here you can access to name property like university.name

                // ...
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException())
            }
        }
        database.child("products").orderByChild("product_id").equalTo(id)
            .addValueEventListener(productListener)
    }

    @SuppressLint("SetTextI18n")
    private fun displayProduct(product: Product) {
        with(product) {
            binding.quantityStepper.setQuantitizerListener(object : QuantitizerListener {
                override fun onIncrease() {
                }

                override fun onDecrease() {

                }

                override fun onValueChanged(value: Int) {
                    binding.tvTotalPrice.text =
                        BigDecimal(value).multiply(BigDecimal(product_price)).toString() + "So'm"
                }
            })

            //price
            binding.tvPrice.text = "$product_price So'm"
            binding.tvTotalPrice.text = "$product_price So'm"
            //image
            product.product_image?.let { setupViewPager(it) }
            //rating
            binding.tvRating.text = product_rating.toString()
            //solds
            binding.tvSold.text = product_sold + " " + context.getString(R.string.sold)
            //title
            binding.tvTitle.text = product_name
            //description
            binding.tvDescription.text = product_description
            if (product_description?.length!! > 70)
                makeTextViewResizable(binding.tvDescription, 3, getString(R.string.view_more), true)

        }
    }

    private fun setupViewPager(images: ArrayList<Image>) {
        viewPagerAdapter = ViewPagerAdapter(images, context)
        viewPager?.currentItem = 0
        viewPager = binding.viewpager
        viewPager?.offscreenPageLimit = 3
        viewPager!!.adapter = viewPagerAdapter
        viewPager!!.clipToPadding = false
        viewPager!!.clipChildren = false
        viewPager!!.getChildAt(0).overScrollMode = View.OVER_SCROLL_NEVER
        binding.dotsIndicator.attachTo(binding.viewpager)
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
}