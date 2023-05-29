package uz.seppuku.vp.fragment

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.SuppressLint
import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
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
import com.youth.banner.adapter.BannerImageAdapter
import com.youth.banner.holder.BannerImageHolder
import com.youth.banner.indicator.CircleIndicator
import dagger.hilt.android.AndroidEntryPoint
import uz.seppuku.vp.activity.NotificationActivity
import uz.seppuku.vp.adapter.CategoryAdapter
import uz.seppuku.vp.adapter.ProductsAdapter
import uz.seppuku.vp.databinding.FragmentHomeBinding
import uz.seppuku.vp.databinding.ItemProductBinding
import uz.seppuku.vp.model.Product
import uz.seppuku.vp.utils.Extensions.toast
import uz.seppuku.vp.utils.Logger
import uz.seppuku.vp.utils.Resource
import uz.seppuku.vp.utils.Utils.hideLoading
import uz.seppuku.vp.utils.Utils.showLoading
import uz.seppuku.vp.viewmodel.HomeViewModel

@AndroidEntryPoint
class HomeFragment : Fragment(R.layout.fragment_home), ProductsAdapter.OnPostItemClickListener,
    CategoryAdapter.OnCategoryItemClickListener {


    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    val TAG: String = "HomeFragment"
    private val viewModel by viewModels<HomeViewModel>()

    //adapter
    var productsAdapter: ProductsAdapter? = null

    //variables
    private var shortAnimationDuration: Int = 0


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentHomeBinding.bind(view)
        shortAnimationDuration = resources.getInteger(android.R.integer.config_shortAnimTime)

        inits()
    }

    override fun onResume() {
        super.onResume()
        viewModel.getAllProducts()
    }

    @SuppressLint("SimpleDateFormat")
    private fun inits() {
        // init product adapter
        productsAdapter = ProductsAdapter(requireContext(), this)

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
                    hideLoading(binding.progressBar)
                }
                Resource.Status.ERROR -> {
                    it.message?.let { it1 -> Logger.e(TAG, it1) }
                    hideLoading(binding.progressBar)
                }
                Resource.Status.LOADING -> {
                    showLoading(binding.progressBar)
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
            .addBannerLifecycleObserver(this).indicator = CircleIndicator(requireContext())

    }


    override fun onPostItemClickListener(id: String, binding: ItemProductBinding) {

       findNavController().navigate(HomeFragmentDirections.actionNavHomeToDetailsFragment(id))
    }

    override fun onCategoryItemClickListener(id: String) {
        toast(id)

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