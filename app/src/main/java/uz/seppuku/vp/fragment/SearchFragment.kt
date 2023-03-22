package uz.seppuku.vp.fragment

import android.annotation.SuppressLint
import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Pair
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.transition.TransitionInflater
import com.google.firebase.database.*
import uz.seppuku.vp.activity.DetailsActivity
import uz.seppuku.vp.adapter.ProductsAdapter
import uz.seppuku.vp.adapter.RecentSearchesAdapter
import uz.seppuku.vp.databinding.FragmentSearchBinding
import uz.seppuku.vp.databinding.ItemProductBinding
import uz.seppuku.vp.manager.MainPrefManager
import uz.seppuku.vp.model.Product
import java.util.*


class SearchFragment : Fragment(), ProductsAdapter.OnPostItemClickListener,
    RecentSearchesAdapter.OnTextClickListener, RecentSearchesAdapter.OnDeleteClickListener {

    lateinit var binding: FragmentSearchBinding
    val TAG: String = SearchFragment::class.java.simpleName
    var database = FirebaseDatabase.getInstance().reference
    lateinit var mContext: Context
    val adapter = ProductsAdapter(requireContext(), this)
    var historyList: ArrayList<String> = ArrayList()
    val productsList: ArrayList<Product> = ArrayList()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSearchBinding.inflate(inflater, container, false)
        mContext = requireContext()
        inits()
        val animation =
            TransitionInflater.from(requireContext()).inflateTransition(android.R.transition.move)
        sharedElementEnterTransition = animation
        sharedElementReturnTransition = animation
        return binding.root
    }

    private fun inits() {
        binding.flSearch.transitionName = "search_field"
        if (MainPrefManager(mContext).loadSearchHistory()?.isNotEmpty() == true) {
            historyList = MainPrefManager(mContext).loadSearchHistory()!!
            refreshRecentAdapter(historyList)
        }


        searchEditTextTextWatcher()
        binding.etSearch.setOnKeyListener(object : View.OnKeyListener {
            override fun onKey(v: View?, keyCode: Int, event: KeyEvent): Boolean {
                // If the event is a key-down event on the "enter" button
                uz.seppuku.vp.utils.Logger.e(TAG, keyCode.toString())
                uz.seppuku.vp.utils.Logger.e(TAG, event.toString())
                if ((event.action == KeyEvent.ACTION_DOWN) &&
                    (keyCode == KeyEvent.KEYCODE_ENTER)
                ) {
                    uz.seppuku.vp.utils.Logger.e(TAG, keyCode.toString())
                    uz.seppuku.vp.utils.Logger.e(TAG, event.toString())
                    val searchedText = binding.etSearch.text.toString()
                    searchProducts(searchedText)
                    historyList.add(searchedText)
                    MainPrefManager(mContext).storeSearchHistory(historyList)
                    return true
                }
                return false
            }
        })
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun refreshSearchedProductsAdapter(products: ArrayList<Product>) {
        binding.rvSearchResults.layoutManager = GridLayoutManager(
            requireContext(), 2,
            GridLayoutManager.VERTICAL,
            false
        )

        binding.rvSearchResults.adapter = adapter


    }

    @SuppressLint("NotifyDataSetChanged")
    private fun refreshRecentAdapter(products: ArrayList<String>) {
        binding.rvSearchHistory.layoutManager = GridLayoutManager(
            requireContext(), 1,
            GridLayoutManager.VERTICAL,
            false
        )
        val adapter = RecentSearchesAdapter(requireContext(), products, this, this)
        binding.rvSearchHistory.adapter = adapter


    }

    private fun searchProducts(searchText: String) {

        val query: Query =
            database.child("products").orderByChild("product_name")
                .startAt(searchText.uppercase(Locale.ROOT))
                .endAt(searchText.lowercase(Locale.ROOT) + "\uf8ff")
        query.addValueEventListener(object : ValueEventListener {
            @SuppressLint("NotifyDataSetChanged")
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    productsList.clear()
                    for (dataSnapshot in snapshot.children) {
                        val product: Product? = dataSnapshot.getValue(Product::class.java)
                        uz.seppuku.vp.utils.Logger.e(TAG, product?.product_name.toString())
                        if (product != null) {
                            productsList.add(product)
                        }
                    }
                    refreshSearchedProductsAdapter(productsList)


                }
            }

            override fun onCancelled(error: DatabaseError) {
                uz.seppuku.vp.utils.Logger.e(TAG, error.message)
            }
        })
    }

    override fun onPostItemClickListener(id: String, binding: ItemProductBinding) {
        val intent = Intent(requireContext(), DetailsActivity::class.java)
        intent.putExtra("id", id)
        val imagePair = Pair.create<View, String>(binding.ivImage, "image_field")
        val titlePair = Pair.create<View, String>(binding.tvTitle, "title_field")
        val pricePair = Pair.create<View, String>(binding.tvPrice, "price_field")
        val ratingPair = Pair.create<View, String>(binding.llRating, "rating_field")
        val soldsPair = Pair.create<View, String>(binding.tvSold, "solds_field")

        val options =
            ActivityOptions.makeSceneTransitionAnimation(
                requireActivity(),
                imagePair
            )
        startActivity(intent, options.toBundle())
    }

    override fun onTextClickListener(text: String) {

    }

    override fun onDeleteClickListener(text: String) {
        historyList.remove(text)
        MainPrefManager(mContext).storeSearchHistory(historyList)
        refreshRecentAdapter(historyList)
    }

    private fun searchEditTextTextWatcher() {

        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (!s.isEmpty()) {
                    binding.rvSearchResults.visibility = View.VISIBLE
                    binding.rvSearchHistory.visibility = View.GONE
                } else {
                    refreshRecentAdapter(historyList)
                    if (productsList.isNotEmpty()){
                        productsList.clear()
                    }
                    binding.rvSearchResults.visibility = View.GONE
                    binding.rvSearchHistory.visibility = View.VISIBLE
                }
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun afterTextChanged(s: Editable) {

            }
        })

    }


}