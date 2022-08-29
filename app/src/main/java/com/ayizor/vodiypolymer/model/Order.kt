package com.ayizor.vodiypolymer.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Order(
    val product_id: String? = null,
    val product_user_id: String? = null,
    val product_name: String? = null,
    val product_description: String? = null,
    val product_total_price: String? = null,
    val product_total_quantity: String? = null,
    val product_discount: String? = null,
    val product_image: ArrayList<Image>? = null,
    val product_isOrdered: Boolean = false,
    val product_step: Int? = null,
    val product_created_at: String? = null,
    val product_updated_at: String? = null
) : Parcelable
