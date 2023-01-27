package uz.seppuku.vp.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import uz.seppuku.vp.R
import uz.seppuku.vp.databinding.ItemChooseLanguageBinding
import uz.seppuku.vp.manager.MainPrefManager
import uz.seppuku.vp.model.Language


class LanguageAdapter(
    var context: Context,
    var arrayList: ArrayList<Language>,
    var onItemClickListener: OnItemClickListener
) : RecyclerView.Adapter<LanguageAdapter.LanguageViewHolder>() {


    val TAG: String = LanguageAdapter::class.java.simpleName
    private lateinit var binding: ItemChooseLanguageBinding
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): LanguageAdapter.LanguageViewHolder {

        binding = ItemChooseLanguageBinding.inflate(LayoutInflater.from(context), parent, false)
        return LanguageViewHolder(binding)


    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onBindViewHolder(
        holder: LanguageViewHolder,
        @SuppressLint("RecyclerView") position: Int
    ) {
        val language = arrayList[position]
        holder.bindLanguage(language)

    }


    override fun getItemCount(): Int {
        // pass total list size
        return arrayList.size
    }

    inner class LanguageViewHolder(val binding: ItemChooseLanguageBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("NotifyDataSetChanged")
        fun bindLanguage(language: Language) {

            with(language) {

                binding.tvTitle.text = language_name

                language_isSelected = MainPrefManager(context).loadLanguage() == language_code

                binding.mainCv.setOnClickListener {
                    notifyDataSetChanged()
                    onItemClickListener.onItemClickListener(language_code.toString())
                }


                if (MainPrefManager(context).loadLanguage() == language_code && language_isSelected==true) {
                    binding.tvSelected.text = context.getString(R.string.current)
                } else {
                    binding.tvSelected.text =""
                }

            }
        }
    }

    interface OnItemClickListener {
        fun onItemClickListener(language_code: String)
    }
}

