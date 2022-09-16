package uz.ayizor.vp.adapter


import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import uz.ayizor.vp.R
import uz.ayizor.vp.model.Category


class CategoryAdapter(
    val context: Context,
    var postsList: ArrayList<Category>,
    private val onCategoryItemClickListener: OnCategoryItemClickListener,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var row_index = 0
    val TAG: String = CategoryAdapter::class.java.simpleName
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_category, parent, false)
        return ProductColorViewHolder(view)


    }

    @SuppressLint("SetTextI18n", "NotifyDataSetChanged")
    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        @SuppressLint("RecyclerView") position: Int
    ) {

        val category: Category = postsList[position]
        if (holder is ProductColorViewHolder) {
            holder.name.text = category.category_name
            holder.mainFl.setOnClickListener {
                row_index = position;
                notifyDataSetChanged();
                if (category.category_id != null) {
                    onCategoryItemClickListener.onCategoryItemClickListener(
                        category.category_id
                    )
                }
            }
            val sdk: Int = android.os.Build.VERSION.SDK_INT;
            if (row_index == position) {
                holder.name.setTextColor(Color.parseColor("#ffffff"))
                if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                    holder.mainFl.setBackgroundDrawable(
                        ContextCompat.getDrawable(
                            context,
                            R.drawable.item_enabled_background
                        )
                    );
                } else {
                    holder.mainFl.background =
                        ContextCompat.getDrawable(context, R.drawable.item_enabled_background);
                }
            } else {
                holder.name.setTextColor(Color.parseColor("#000000"))
                if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                    holder.mainFl.setBackgroundDrawable(
                        ContextCompat.getDrawable(
                            context,
                            R.drawable.item_disabled_background
                        )
                    );
                } else {
                    holder.mainFl.background =
                        ContextCompat.getDrawable(context, R.drawable.item_disabled_background);
                }

            }

        }


    }

    override fun getItemCount(): Int {
        return postsList.size
    }


    class ProductColorViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var name: TextView
        var mainFl: FrameLayout


        init {
            name = itemView.findViewById(R.id.tv_name)
            mainFl = itemView.findViewById(R.id.main_fl)
        }
    }

    interface OnCategoryItemClickListener {
        fun onCategoryItemClickListener(id: String)
    }

}