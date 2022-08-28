package com.ayizor.vodiypolymer.model.listmodel

import android.os.Parcelable
import com.ayizor.vodiypolymer.model.Order
import kotlinx.parcelize.Parcelize

@Parcelize
data class OrdersList(val ordersList: ArrayList<Order>) : Parcelable
