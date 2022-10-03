package uz.ayizor.vp.model.listmodel

import android.os.Parcelable
import uz.ayizor.vp.model.Order
import kotlinx.parcelize.Parcelize
import uz.ayizor.vp.model.Cart

@Parcelize
data class OrdersList(val ordersList: ArrayList<Cart>) : Parcelable
