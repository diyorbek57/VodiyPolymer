package uz.seppuku.vp.model.listmodel

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import uz.seppuku.vp.model.Cart

@Parcelize
data class OrdersList(val ordersList: ArrayList<Cart>) : Parcelable
