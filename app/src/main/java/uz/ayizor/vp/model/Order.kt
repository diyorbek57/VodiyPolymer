package uz.ayizor.vp.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Order(
    val order_id: String? = null,
    val order_user_id: String? = null,
    var order_location_id: String? = null,
    var order_product_id: String? = null,
    var order_isOrdered: Boolean = false,
    var order_step: Int? = null,
    val order_total_price: String? = null,
    val order_total_quantity: String? = null,
    val order_created_at: String? = null,
    val order_updated_at: String? = null,
    val order_courier_id: String? = null
) : Parcelable
