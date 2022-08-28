package com.ayizor.vodiypolymer.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.ayizor.afeme.utils.Logger
import com.ayizor.vodiypolymer.activity.DetailsActivity
import com.ayizor.vodiypolymer.activity.NotificationActivity
import com.ayizor.vodiypolymer.adapter.CategoryAdapter
import com.ayizor.vodiypolymer.adapter.ProductsAdapter
import com.ayizor.vodiypolymer.databinding.FragmentHomeBinding
import com.ayizor.vodiypolymer.model.Category
import com.ayizor.vodiypolymer.model.Color
import com.ayizor.vodiypolymer.model.Image
import com.ayizor.vodiypolymer.model.Product
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.youth.banner.adapter.BannerImageAdapter
import com.youth.banner.holder.BannerImageHolder
import com.youth.banner.indicator.CircleIndicator


class HomeFragment : Fragment(), ProductsAdapter.OnPostItemClickListener, CategoryAdapter.OnCategoryItemClickListener {

    lateinit var binding: FragmentHomeBinding
    val TAG: String = HomeFragment::class.java.simpleName

    //variables
    val test_image_url =
        "https://static.remove.bg/remove-bg-web/f9c9a2813e0321c04d66062f8cca92aedbefced7/assets/start_remove-c851bdf8d3127a24e2d137a55b1b427378cd17385b01aec6e59d5d4b5f39d2ec.png"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        inits()
        return binding.root
    }

    @SuppressLint("SimpleDateFormat")
    private fun inits() {

        binding.rvProducts.layoutManager = GridLayoutManager(
            requireContext(), 2,
            GridLayoutManager.VERTICAL,
            false
        )
        binding.rvCategories.layoutManager = GridLayoutManager(
            requireContext(), 1,
            GridLayoutManager.HORIZONTAL,
            false
        )

        binding.ivNotifications.setOnClickListener {
            val intent = Intent(requireContext(), NotificationActivity::class.java)
            startActivity(intent)
        }
        getDiscountProducts()
        getProducts()
        getCategory()
    }

    private fun setupBanner(product: ArrayList<Product>) {

        binding.vpBanner.setAdapter(object :
            BannerImageAdapter<Product?>(product as List<Product?>?) {

            override fun onBindView(
                holder: BannerImageHolder?,
                data: Product?,
                position: Int,
                size: Int
            ) {
                holder?.itemView?.let {
                    Glide.with(it)
                        .load(data?.product_image?.get(0)?.image_url.toString())
                        .apply(RequestOptions.bitmapTransform(RoundedCorners(30)))
                        .into(holder.imageView)
                }
            }
        })
            .addBannerLifecycleObserver(this) //添加生命周期观察者
            .setIndicator(CircleIndicator(requireContext()))

    }

    private fun refreshProductsAdapter(products: ArrayList<Product>) {
        val adapter = ProductsAdapter(requireContext(), products, this)
        binding.rvProducts.adapter = adapter
//        binding.progressBar.visibility = View.GONE
//        binding.llMain.visibility = View.VISIBLE

    }

    private fun refreshCategoryAdapter(category: ArrayList<Category>) {
        val adapter = CategoryAdapter(requireContext(), category, this)
        binding.rvCategories.adapter = adapter
//        binding.progressBar.visibility = View.GONE
//        binding.llMain.visibility = View.VISIBLE

    }

    private fun getProducts() {
        val colorList: ArrayList<Color> = ArrayList()
        colorList.add(Color("1", "FFBB86FC"))
        colorList.add(Color("2", "FF6200EE"))
        colorList.add(Color("3", "FF03DAC5"))
        val productsList: ArrayList<Product> = ArrayList()
        val imageList: ArrayList<Image> = ArrayList()
        for (i in 0 until 5) {
            imageList.add(Image("2", test_image_url, "0", "0"))
        }
        for (i in 0 until 15) {
            productsList.add(
                Product(
                    "1", "2", colorList, "Test",
                    "ewgbnewigboirqwgboqriugbwriuqbfnwenweoigfyweqbofeiuwnfwieqfboiewqubg",
                    "2000", 2.6, "13", 0, imageList, "0", "0"
                )
            )
        }
        refreshProductsAdapter(productsList)

    }

    private fun getCategory() {
        val categoryList: ArrayList<Category> = ArrayList()

        for (i in 0 until 15) {
            categoryList.add(
                Category(
                    i.toString(), "0", "0"
                )
            )
        }
        refreshCategoryAdapter(categoryList)

    }

    private fun getDiscountProducts() {
        val colorList: ArrayList<Color> = ArrayList()
        colorList.add(Color("1", "FFBB86FC"))
        colorList.add(Color("2", "FF6200EE"))
        colorList.add(Color("3", "FF03DAC5"))
        val productsList: ArrayList<Product> = ArrayList()
        val imageList: ArrayList<Image> = ArrayList()
        for (i in 0 until 5) {
            imageList.add(Image("2", test_image_url, "0", "0"))
        }
        for (i in 0 until 15) {
            productsList.add(
                Product(
                    "1", "2", colorList, "Test",
                    "ewgbnewigboirqwgboqriugbwriuqbfnwenweoigfyweqbofeiuwnfwieqfboiewqubg",
                    "2000", 2.6, "13", 0, imageList, "0", "0"
                )
            )
        }
        setupBanner(productsList)

    }

    override fun onPostItemClickListener(id: String) {
        val intent = Intent(requireContext(), DetailsActivity::class.java)
        intent.putExtra("id", id)
        startActivity(intent)
    }

    override fun onCategoryItemClickListener(id: String) {
        Logger.d(TAG, id)
    }


}