package com.ayizor.vodiypolymer.model

data class Product(
    val product_id: String? = null,
    val product_category_id: String? = null,
    val product_colors: ArrayList<Color>? = null,
    val product_name: String? = null,
    val product_description: String? = null,
    val product_price: String? = null,
    val product_rating: Double? = null,
    val product_sold: String? = null,
    val product_discount: Int? = null,
    val product_image: ArrayList<Image>? = null,
    val product_created_at: String? = null,
    val product_updated_at: String? = null
)