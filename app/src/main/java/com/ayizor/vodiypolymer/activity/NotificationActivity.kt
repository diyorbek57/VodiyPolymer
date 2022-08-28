package com.ayizor.vodiypolymer.activity

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.ayizor.vodiypolymer.adapter.NotificationsAdapter
import com.ayizor.vodiypolymer.databinding.ActivityNotificationBinding
import com.ayizor.vodiypolymer.model.Notification
import com.shuhart.stickyheader.StickyHeaderItemDecorator


class NotificationActivity : BaseActivity() {

    lateinit var binding: ActivityNotificationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNotificationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        inits()

    }

    private fun inits() {
        binding.rvNotifications.layoutManager = LinearLayoutManager(
            this,
            LinearLayoutManager.VERTICAL,
            false
        )
    }
    private fun refreshPostsAdapter(notifications: ArrayList<Notification>) {
        val adapter = NotificationsAdapter(this, notifications)
        val decorator = StickyHeaderItemDecorator(adapter)
        decorator.attachToRecyclerView(binding.rvNotifications)
        binding.rvNotifications.adapter = adapter
//        binding.progressBar.visibility = View.GONE
//        binding.llMain.visibility = View.VISIBLE

    }

}