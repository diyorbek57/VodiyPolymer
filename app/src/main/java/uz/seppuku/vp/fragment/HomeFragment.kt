package uz.seppuku.vp.fragment

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.SuppressLint
import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Pair.create
import android.view.View
import uz.seppuku.vp.R
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.database.*
import com.google.firebase.database.annotations.NotNull
import com.youth.banner.adapter.BannerImageAdapter
import com.youth.banner.holder.BannerImageHolder
import com.youth.banner.indicator.CircleIndicator
import dagger.hilt.android.AndroidEntryPoint
import uz.seppuku.vp.activity.NotificationActivity
import uz.seppuku.vp.adapter.CategoryAdapter
import uz.seppuku.vp.adapter.ProductsAdapter
import uz.seppuku.vp.databinding.FragmentHomeBinding
import uz.seppuku.vp.databinding.ItemProductBinding
import uz.seppuku.vp.model.Category
import uz.seppuku.vp.model.Product
import uz.seppuku.vp.utils.Logger
import uz.seppuku.vp.utils.Resource
import uz.seppuku.vp.viewmodel.HomeViewModel

@AndroidEntryPoint
class HomeFragment : Fragment(R.layout.fragment_home), ProductsAdapter.OnPostItemClickListener,
    CategoryAdapter.OnCategoryItemClickListener {


    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    val TAG: String = HomeFragment::class.java.simpleName
    private val viewModel by viewModels<HomeViewModel>()

    //adapter
    var productsAdapter: ProductsAdapter? = null

    //variables
    lateinit var product: Product
    lateinit var category: Category
    var database = FirebaseDatabase.getInstance()
    private var shortAnimationDuration: Int = 0


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentHomeBinding.bind(view)
        shortAnimationDuration = resources.getInteger(android.R.integer.config_shortAnimTime)
        productsAdapter = ProductsAdapter(requireContext(), this)
        inits()
    }

    override fun onResume() {
        super.onResume()

        viewModel.getAllProducts()
    }

    @SuppressLint("SimpleDateFormat")
    private fun inits() {
        initUI()
        observer()


    }

    private fun initUI() {
        binding.apply {

            rvProducts.layoutManager = GridLayoutManager(
                requireContext(), 2,
                GridLayoutManager.VERTICAL,
                false
            )
            rvProducts.adapter = productsAdapter
            rvCategories.layoutManager = GridLayoutManager(
                requireContext(), 1,
                GridLayoutManager.HORIZONTAL,
                false
            )

            ivNotifications.setOnClickListener {
                val intent = Intent(requireContext(), NotificationActivity::class.java)
                startActivity(intent)
            }
            flSearch.setOnClickListener {
                val extras = FragmentNavigatorExtras(binding.flSearch to "search_field")
                findNavController().navigate(
                    R.id.action_nav_home_to_searchFragment,
                    null,
                    null,
                    extras
                )

            }

            ivFavorites.setOnClickListener {
            }
        }
    }

    private fun observer() {
        viewModel.products.observe(viewLifecycleOwner) {
            when (it.status) {
                Resource.Status.SUCCESS -> {
                    productsAdapter?.submitList(it.data)
                }
                Resource.Status.ERROR -> {
                    //      it.data?.status?.let { it1 -> Log.e(TAG, it1) }
//                    hideLoading(bn.progressbar, bn.btnSignIn)
                }
                Resource.Status.LOADING -> {
                    //  showLoading(bn.progressbar, bn.btnSignIn)
                }
            }
        }
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


    private fun refreshCategoryAdapter(category: ArrayList<Category>) {
        val adapter = CategoryAdapter(requireContext(), category, this)
        binding.rvCategories.adapter = adapter
//        binding.progressBar.visibility = View.GONE
//        binding.llMain.visibility = View.VISIBLE

    }


    private fun getCategory() {
        val categoriesList: ArrayList<Category> = ArrayList()
        val reference = database.getReference("categories")

        reference.addValueEventListener(object : ValueEventListener {
            @SuppressLint("SetTextI18n")
            override fun onDataChange(@NotNull snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    categoriesList.clear()
                    categoriesList.add(
                        Category(
                            "all",
                            getString(uz.seppuku.vp.R.string.all),
                            null,
                            null
                        )
                    )
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
        val reference = database.getReference("products")
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
                } else {
                    Logger.e(TAG, "getProductByCategory: snapshot = null ")

                }
            }

            override fun onCancelled(@NotNull error: DatabaseError) {
                Logger.e(TAG, error.message)
            }
        })


    }

    override fun onPostItemClickListener(id: String, binding: ItemProductBinding) {
        val intent = Intent(requireContext(), uz.seppuku.vp.activity.DetailsActivity::class.java)
        intent.putExtra("id", id)
        val imagePair = create<View, String>(binding.ivImage, "image_field")
        val titlePair = create<View, String>(binding.tvTitle, "title_field")
        val pricePair = create<View, String>(binding.tvPrice, "price_field")
        val ratingPair = create<View, String>(binding.llRating, "rating_field")
        val soldsPair = create<View, String>(binding.tvSold, "solds_field")

        val options =
            ActivityOptions.makeSceneTransitionAnimation(
                requireActivity(),
                imagePair
            )
        startActivity(intent, options.toBundle())
    }

    override fun onCategoryItemClickListener(id: String) {
        if (id.contentEquals("all")) {

        } else {

        }

    }

    private fun showViewWithAnimations(view: View) {
        view.apply {
            // Set the content view to 0% opacity but visible, so that it is visible
            // (but fully transparent) during the animation.
            alpha = 0f
            visibility = View.VISIBLE

            // Animate the content view to 100% opacity, and clear any animation
            // listener set on the view.
            animate()
                .alpha(1f)
                .setDuration(shortAnimationDuration.toLong())
                .setListener(null)
        }
        // Animate the loading view to 0% opacity. After the animation ends,
        // set its visibility to GONE as an optimization step (it won't
        // participate in layout passes, etc.)
        binding.progressBar.animate()
            .alpha(0f)
            .setDuration(shortAnimationDuration.toLong())
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    binding.progressBar.visibility = View.GONE
                }
            })

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}