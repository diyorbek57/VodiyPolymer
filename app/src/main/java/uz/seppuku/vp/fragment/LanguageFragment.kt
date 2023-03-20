package uz.seppuku.vp.fragment

import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.SimpleItemAnimator
import uz.seppuku.vp.R
import uz.seppuku.vp.adapter.LanguageAdapter
import uz.seppuku.vp.databinding.FragmentLanguageBinding
import uz.seppuku.vp.manager.MainPrefManager
import uz.seppuku.vp.model.Language
import uz.seppuku.vp.utils.Utils


class LanguageFragment : Fragment(R.layout.fragment_language), LanguageAdapter.OnItemClickListener {

    lateinit var binding: FragmentLanguageBinding
    val TAG: String = LanguageFragment::class.java.simpleName


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentLanguageBinding.bind(view)
    }

    override fun onResume() {
        super.onResume()
        inits()
    }

    private fun inits() {
        binding.rvLanguage.layoutManager = LinearLayoutManager(
            requireContext(),
            LinearLayoutManager.VERTICAL,
            false
        )
        binding.ivBack.setOnClickListener {
            findNavController().navigateUp()
        }
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
        languages.add(Language("English", "en", false))
        languages.add(Language("O'zbek", "uz", false))
        languages.add(Language("Русский", "ru", false))
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