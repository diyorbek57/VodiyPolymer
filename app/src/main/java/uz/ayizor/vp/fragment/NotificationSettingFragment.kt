package uz.ayizor.vp.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import uz.ayizor.vp.R
import uz.ayizor.vp.databinding.FragmentNotificationSettingBinding


class NotificationSettingFragment : Fragment(R.layout.fragment_notification_setting) {

    lateinit var binding: FragmentNotificationSettingBinding
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentNotificationSettingBinding.bind(view)

        inits()
    }

    private fun inits() {

    }
}