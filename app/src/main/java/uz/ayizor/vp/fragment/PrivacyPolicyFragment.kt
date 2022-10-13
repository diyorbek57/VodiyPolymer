package uz.ayizor.vp.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import uz.ayizor.vp.R
import uz.ayizor.vp.databinding.FragmentPrivacyPolicyBinding


class PrivacyPolicyFragment : Fragment(R.layout.fragment_privacy_policy) {

    lateinit var binding: FragmentPrivacyPolicyBinding
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentPrivacyPolicyBinding.bind(view)

        inits()
    }


    @SuppressLint("SetJavaScriptEnabled")
    private fun inits() {
       // binding.wvPrivacy.loadUrl("https://firebasestorage.googleapis.com/v0/b/vodiy-polymer.appspot.com/o/Privacy%20Policy%2FPrivacy%20Policy.pdf?alt=media&token=1b287602-6d41-4356-bdc0-dd12ebbed075")

    }
}