package uz.seppuku.vp.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import uz.seppuku.vp.databinding.ItemRecentSearchesBinding

class RecentSearchesAdapter(
    val context: Context,
    private var postsList: ArrayList<String>,
    private val onTextClickListener: OnTextClickListener,
    private val onDeleteClickListener: OnDeleteClickListener
) : RecyclerView.Adapter<RecentSearchesAdapter.RecentSearchViewHolder>() {


    val TAG: String = ProductsAdapter::class.java.simpleName
    private lateinit var binding: ItemRecentSearchesBinding
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecentSearchViewHolder {

        binding = ItemRecentSearchesBinding.inflate(LayoutInflater.from(context), parent, false)
        return RecentSearchViewHolder(binding)


    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RecentSearchViewHolder, position: Int) {
        val text: String = postsList[position]
        holder.bindProduct(text)

    }

    override fun getItemCount(): Int {
        return postsList.size
    }


    inner class RecentSearchViewHolder(val binding: ItemRecentSearchesBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindProduct(text: String) {

            with(text) {

                binding.tvTitle.text = text
                binding.llMain.setOnClickListener {
                    onTextClickListener.onTextClickListener(text)
                }
                binding.ivClose.setOnClickListener {
                    onDeleteClickListener.onDeleteClickListener(text)
                }
            }
        }
    }

    interface OnTextClickListener {
        fun onTextClickListener(text: String)
    }

    interface OnDeleteClickListener {
        fun onDeleteClickListener(text: String)
    }

}