package uz.ayizor.vp.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import uz.ayizor.vp.adapter.LanguageAdapter
import uz.ayizor.vp.databinding.FragmentLanguageBinding
import uz.ayizor.vp.manager.MainPrefManager
import uz.ayizor.vp.model.Language


class LanguageFragment : Fragment(), LanguageAdapter.OnRadioButtonClickListener {

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
//        binding.progressBar.visibility = View.GONE
//        binding.llMain.visibility = View.VISIBLE

    }

    private fun createLanguages() {
        val languages: ArrayList<Language> = ArrayList()
        languages.add(Language("English", "en", "0"))
        languages.add(Language("O'zbek", "uz", "1"))
        languages.add(Language("Русский", "ru", "2"))
        refreshCategoryAdapter(languages)
    }


    override fun onRadioButtonClickListener(code: String) {
        MainPrefManager(requireContext()).storeLanguage(code)
    }


}