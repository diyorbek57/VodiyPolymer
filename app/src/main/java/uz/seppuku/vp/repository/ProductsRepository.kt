package uz.seppuku.vp.repository

import uz.seppuku.vp.model.Product
import uz.seppuku.vp.utils.NetworkResponse

interface ProductsRepository {


    suspend fun getAllProducts(): NetworkResponse<List<Product>>
    suspend fun getProductByCategory(categoryId: String): List<Product>
    suspend fun getSingleProduct(productId: String): Product

}