package uz.seppuku.vp.adapter


import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout

import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import uz.seppuku.vp.R
import uz.seppuku.vp.databinding.ItemCategoryBinding
import uz.seppuku.vp.databinding.ItemProductBinding
import uz.seppuku.vp.model.Category
import uz.seppuku.vp.model.Product


class CategoryAdapter(
    val context: Context,
    val onCategoryItemClickListener: OnCategoryItemClickListener,
) : ListAdapter<Category, CategoryAdapter.ViewHolder>(DiffCallback()) {


    val TAG: String = "ProductsAdapter"
    private lateinit var binding: ItemCategoryBinding
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        binding = ItemCategoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)


    }


    override fun onBindViewHolder(
        holder: ViewHolder, position: Int
    ) {
        val currentItem = getItem(position)
        holder.bind(currentItem)
    }


    inner class ViewHolder(val binding: ItemCategoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(category: Category) {
            var row_index = 0

            category.apply {
                binding.apply {
                    tvName.text = category_name

                    mainFl.setOnClickListener {
                        row_index = adapterPosition;

                        if (category.category_id != null) {
                            onCategoryItemClickListener.onCategoryItemClickListener(
                                category.category_id
                            )
                        }
                        if (row_index == position) {
                            tvName.setTextColor(Color.parseColor("#ffffff"))


                            mainFl.background = ContextCompat.getDrawable(
                                context,
                                R.drawable.item_enabled_background
                            );
                        } else {
                           tvName.setTextColor(Color.parseColor("#000000"))

                            mainFl.background =
                                ContextCompat.getDrawable(
                                    context,
                                    R.drawable.item_disabled_background
                                )

                        }
                    }
                }
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<Category>() {
        override fun areItemsTheSame(oldItem: Category, newItem: Category) =
            oldItem.category_id == newItem.category_id

        override fun areContentsTheSame(oldItem: Category, newItem: Category) =
            oldItem == newItem

    }

    interface OnCategoryItemClickListener {
        fun onCategoryItemClickListener(id: String)
    }
}

