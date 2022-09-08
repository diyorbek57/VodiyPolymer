package uz.ayizor.vp.model.listmodel

import android.os.Parcelable
import uz.ayizor.vp.model.Order
import kotlinx.parcelize.Parcelize

@Parcelize
data class OrdersList(val ordersList: ArrayList<Order>) : Parcelable
