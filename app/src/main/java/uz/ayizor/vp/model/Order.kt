package uz.ayizor.vp.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Order(
    val order_id: String? = null,
    val ordert_user_id: String? = null,
    var order_location: Location? = null,
    var order_products: ArrayList<Cart>? = null,
    var order_isOrdered: Boolean = false,
    var order_step: Int? = null,
    val order_created_at: String? = null,
    val order_updated_at: String? = null,
    val order_courier_id: String? = null
) : Parcelable
