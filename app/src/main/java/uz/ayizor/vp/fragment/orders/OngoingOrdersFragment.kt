package uz.ayizor.vp.fragment.orders

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.*
import com.google.firebase.database.annotations.NotNull
import uz.ayizor.vp.adapter.OrderAdapter
import uz.ayizor.vp.databinding.FragmentOngoingBinding
import uz.ayizor.vp.fragment.OrdersFragmentDirections
import uz.ayizor.vp.manager.UserPrefManager
import uz.ayizor.vp.model.Cart
import uz.ayizor.vp.model.Order

class OngoingOrdersFragment : Fragment(), OrderAdapter.OnActionButtonClickListener {

    lateinit var binding: FragmentOngoingBinding
    val TAG: String = OngoingOrdersFragment::class.java.simpleName
    lateinit var product: Order
    val productsList: ArrayList<Order> = ArrayList()
    lateinit var mContext: Context
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentOngoingBinding.inflate(inflater, container, false)
        mContext = requireContext()
        inits()
        return binding.root
    }

    private fun inits() {
        binding.rvOngoing.layoutManager = LinearLayoutManager(
            mContext,
            LinearLayoutManager.VERTICAL,
            false
        )
        getOrders()
    }


    private fun getOrders() {
        val reference = FirebaseDatabase.getInstance().getReference("orders")
        val query: Query =
            reference.orderByChild("product_user_id")
                .equalTo(UserPrefManager(mContext).loadUser()?.user_id)
        query.addValueEventListener(object : ValueEventListener {
            @SuppressLint("SetTextI18n")
            override fun onDataChange(@NotNull snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    productsList.clear()
                    for (userSnapshot in snapshot.children) {

                        product = userSnapshot.getValue(Order::class.java)!!
                        if (product != null) {
                            if (product.order_step!! > 0 && product.order_isOrdered)
                                productsList.add(product)
                        }


                    }
                    if (productsList.isNotEmpty()) {
                        refreshOrdersAdapter(productsList)
                    } else {
                        binding.progressBar.visibility = View.GONE
                        binding.emptyState.llEmpty.visibility = View.VISIBLE
                    }


                } else {
                    binding.progressBar.visibility = View.GONE
                    binding.emptyState.llEmpty.visibility = View.VISIBLE
                }
            }

            override fun onCancelled(@NotNull error: DatabaseError) {}
        })
    }

    private fun refreshOrdersAdapter(products: ArrayList<Order>) {
        val adapter = OrderAdapter(mContext, products, this)
        binding.rvOngoing.adapter = adapter
        binding.progressBar.visibility = View.GONE

    }

    override fun onActionButtonClickListener(product: Cart, order_step: Int) {
        val action = OrdersFragmentDirections.actionNavOrdersToTrackOrderFragment(product,order_step)
        findNavController().navigate(action)
    }


}