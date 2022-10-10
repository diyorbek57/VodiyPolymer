package uz.ayizor.vp.fragment

import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.SimpleItemAnimator
import uz.ayizor.vp.R
import uz.ayizor.vp.adapter.LanguageAdapter
import uz.ayizor.vp.databinding.FragmentLanguageBinding
import uz.ayizor.vp.manager.MainPrefManager
import uz.ayizor.vp.model.Language
import uz.ayizor.vp.utils.Utils


class LanguageFragment : Fragment(), LanguageAdapter.OnItemClickListener {

    lateinit var binding: FragmentLanguageBinding
    val TAG: String = HomeFragment::class.java.simpleName

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLanguageBinding.inflate(inflater, container, false)
        inits()
        return binding.root
    }

    private fun inits() {
        binding.rvLanguage.layoutManager = LinearLayoutManager(
            requireContext(),
            LinearLayoutManager.VERTICAL,
            false
        )
        createLanguages()
    }

    private fun refreshCategoryAdapter(languages: ArrayList<Language>) {
        val adapter = LanguageAdapter(requireContext(), languages, this)
        binding.rvLanguage.adapter = adapter

        (binding.rvLanguage.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false

//        binding.progressBar.visibility = View.GONE
//        binding.llMain.visibility = View.VISIBLE

    }

    private fun createLanguages() {
        val languages: ArrayList<Language> = ArrayList()
        languages.add(Language("English", "en",false))
        languages.add(Language("O'zbek", "uz",false))
        languages.add(Language("Русский", "ru",false))
        refreshCategoryAdapter(languages)
    }


    override fun onItemClickListener(language_code: String) {
        MainPrefManager(requireContext()).storeLanguage(language_code)

        val configuration = Utils.setLocaleLanguage(requireActivity(), language_code)
        onConfigurationChanged(configuration)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        binding.tvTitle.text = requireContext().getString(R.string.language)

    }


}