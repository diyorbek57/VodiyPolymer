package uz.seppuku.vp.fragment.orders

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import uz.seppuku.vp.R
import uz.seppuku.vp.databinding.FragmentTrackOrderBinding
import uz.seppuku.vp.model.Cart
import uz.seppuku.vp.model.Product


class TrackOrderFragment : Fragment() {

    lateinit var binding: FragmentTrackOrderBinding
    val TAG: String = TrackOrderFragment::class.java.simpleName
    private val args: TrackOrderFragmentArgs by navArgs()
    val ref = FirebaseDatabase.getInstance().reference
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


        binding.ivBack.setOnClickListener {
            activity?.onBackPressed()
        }
        val order = args.order
        getProduct(order.order_product_id.toString())
    }

    @SuppressLint("SetTextI18n")
    private fun displayDatas(product: Product) {
        val order = args.order
        with(product) {

                when (order.order_step) {
                    1 -> {
                        binding.tvTrackStatus.text = getString(R.string.order_confirmed)
                        binding.ivTrackStatus.setImageResource(R.drawable.img_track_1)
                    }
                    2 -> {
                        binding.tvTrackStatus.text = getString(R.string.order_shipped)
                        binding.ivTrackStatus.setImageResource(R.drawable.img_track_2)
                    }
                    else -> {
                        binding.tvTrackStatus.text = getString(R.string.order_delivered)
                        binding.ivTrackStatus.setImageResource(R.drawable.img_track_3)
                    }
                }
                //product datas
                binding.tvPrice.text = order.order_total_price
                binding.tvQuantity.text = order.order_total_quantity
                binding.tvTitle.text =product_name
                Glide.with(requireContext())
                    .load(product_image?.get(0)?.image_url)
                    .into(binding.ivImage)

        }
    }

    fun getProduct(product_id: String) {
        var product: Product? = null
        val productListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (postSnapshot in dataSnapshot.children) {

                    product = postSnapshot.getValue(Product::class.java)

                }
                product?.let { displayDatas(it) }


            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost: onCancelled", databaseError.toException())
            }
        }
        ref.child("products").orderByChild("product_id").equalTo(product_id)
            .addValueEventListener(productListener)
    }

}