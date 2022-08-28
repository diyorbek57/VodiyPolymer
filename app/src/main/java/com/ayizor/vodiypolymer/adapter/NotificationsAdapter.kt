package com.ayizor.vodiypolymer.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ayizor.vodiypolymer.model.Notification
import com.shuhart.stickyheader.StickyAdapter

class NotificationsAdapter(val context: Context, val notificationsList: ArrayList<Notification>) :
    StickyAdapter<NotificationsAdapter.HeaderViewHolder, NotificationsAdapter.ChildViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChildViewHolder {
        TODO("Not yet implemented")
    }

    override fun onBindViewHolder(holder: ChildViewHolder, position: Int) {
        TODO("Not yet implemented")
    }

    override fun getItemCount(): Int {
        TODO("Not yet implemented")
    }

    override fun getHeaderPositionForItem(itemPosition: Int): Int {
        TODO("Not yet implemented")
    }

    override fun onBindHeaderViewHolder(holder: HeaderViewHolder?, headerPosition: Int) {
        TODO("Not yet implemented")
    }

    override fun onCreateHeaderViewHolder(parent: ViewGroup?): HeaderViewHolder {
        TODO("Not yet implemented")
    }

    class HeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    }

    class ChildViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    }
}