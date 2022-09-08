package uz.ayizor.vp.adapter


import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import uz.ayizor.vp.databinding.ItemViewPagerBinding
import uz.ayizor.vp.model.Image
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions


class ViewPagerAdapter(
    val postsList: ArrayList<Image>,
    val context: Context
) :
    RecyclerView.Adapter<ViewPagerAdapter.ItemPostViewPagerViewHolder>() {

    val TAG: String = ViewPagerAdapter::class.java.simpleName
    private lateinit var binding: ItemViewPagerBinding
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemPostViewPagerViewHolder {
        binding = ItemViewPagerBinding.inflate(LayoutInflater.from(context), parent, false)
        return ItemPostViewPagerViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ItemPostViewPagerViewHolder, position: Int) {

        with(holder) {
            with(postsList[position]) {
                    Glide.with(context)
                        .load(image_url)
                        .transition(DrawableTransitionOptions.withCrossFade())
                        .into(binding.ivViewpager)


            }
        }


    }

    override fun getItemCount(): Int {
        return postsList.size
    }

    inner class ItemPostViewPagerViewHolder(val binding: ItemViewPagerBinding) :
        RecyclerView.ViewHolder(binding.root)

    interface OnViewPagerItemClickListener {
        fun onViewPagerItemClickListener(id: Int)
    }

}