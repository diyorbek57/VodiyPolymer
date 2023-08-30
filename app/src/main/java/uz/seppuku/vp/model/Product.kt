package uz.seppuku.vp.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Serializable
@Parcelize
data class Product(
    val product_id: String? = null,
    val product_category_id: String? = null,
    val product_name: String? = null,
    val product_description: String? = null,
    val product_price: String? = null,
    val product_discount_price: String? = null,
    val product_unit_of_measurement: String? = null,
    val product_rating: String? = null,
    val product_sold: String? = null,
    val product_out_of_stock: Boolean? = null,
    val product_discount: String? = null,
    val product_image: ArrayList<Image>? = null,
    val product_created_at: String? = null,
    val product_updated_at: String? = null
) : Parcelable