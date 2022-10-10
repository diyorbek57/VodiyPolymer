package uz.ayizor.vp.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Cart(
    val cart_id: String? = null,
    val cart_product_id: String? = null,
    val cart_user_id: String? = null,
    val cart_product_total_price: String? = null,
    val cart_product_total_quantity: String? = null,
    val cart_product_created_at: String? = null,
    val cart_product_updated_at: String? = null
) : Parcelable
