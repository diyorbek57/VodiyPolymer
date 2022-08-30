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
import com.ayizor.vodiypolymer.model.Product
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.database.*
import com.google.firebase.database.annotations.NotNull
import com.youth.banner.adapter.BannerImageAdapter
import com.youth.banner.holder.BannerImageHolder
import com.youth.banner.indicator.CircleIndicator


class HomeFragment : Fragment(), ProductsAdapter.OnPostItemClickListener,
    CategoryAdapter.OnCategoryItemClickListener {

    lateinit var binding: FragmentHomeBinding
    val TAG: String = HomeFragment::class.java.simpleName
    lateinit var product: Product
    lateinit var category: Category

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
        getProducts(null)
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

    private fun getProducts(category_id: String?) {
        if (!category_id.isNullOrEmpty()) {
            getProductByCategory(category_id)
        } else {
            getAllProducts()
        }
    }

    private fun getAllProducts() {
        val productsList: ArrayList<Product> = ArrayList()
        val reference = FirebaseDatabase.getInstance().getReference("products")

        reference.addValueEventListener(object : ValueEventListener {
            @SuppressLint("SetTextI18n")
            override fun onDataChange(@NotNull snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    productsList.clear()
                    for (userSnapshot in snapshot.children) {
                        product = userSnapshot.getValue(Product::class.java)!!
                        if (product != null) {
                            productsList.add(product)
                        }
                    }

                    refreshProductsAdapter(productsList)
                }
            }

            override fun onCancelled(@NotNull error: DatabaseError) {}
        })
    }

    private fun getProductByCategory(categoryId: String?) {
        val productsList: ArrayList<Product> = ArrayList()
        val reference = FirebaseDatabase.getInstance().getReference("products")
        val query: Query =
            reference.orderByChild("product_category_id")
                .equalTo(categoryId)
        query.addValueEventListener(object : ValueEventListener {
            @SuppressLint("SetTextI18n")
            override fun onDataChange(@NotNull snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    productsList.clear()
                    for (userSnapshot in snapshot.children) {
                        product = userSnapshot.getValue(Product::class.java)!!
                        if (product != null) {
                            productsList.add(product)
                        }
                    }

                    refreshProductsAdapter(productsList)
                }
            }

            override fun onCancelled(@NotNull error: DatabaseError) {}
        })
    }

    private fun getCategory() {
        val categoriesList: ArrayList<Category> = ArrayList()
        val reference = FirebaseDatabase.getInstance().getReference("categories")

        reference.addValueEventListener(object : ValueEventListener {
            @SuppressLint("SetTextI18n")
            override fun onDataChange(@NotNull snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    categoriesList.clear()
                    for (userSnapshot in snapshot.children) {
                        category = userSnapshot.getValue(Category::class.java)!!
                        if (category != null) {
                            categoriesList.add(category)
                        }
                    }

                    refreshCategoryAdapter(categoriesList)
                }
            }

            override fun onCancelled(@NotNull error: DatabaseError) {}
        })


    }

    private fun getDiscountProducts() {
        val productsList: ArrayList<Product> = ArrayList()
        val reference = FirebaseDatabase.getInstance().getReference("products")
        val query: Query =
            reference.orderByChild("product_discount").startAfter("0")
        query.addValueEventListener(object : ValueEventListener {
            @SuppressLint("SetTextI18n")
            override fun onDataChange(@NotNull snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    productsList.clear()
                    for (userSnapshot in snapshot.children) {
                        product = userSnapshot.getValue(Product::class.java)!!
                        if (product != null) {
                            productsList.add(product)
                        }
                    }
                    setupBanner(productsList)
                }
            }

            override fun onCancelled(@NotNull error: DatabaseError) {}
        })


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