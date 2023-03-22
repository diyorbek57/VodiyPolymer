package uz.seppuku.vp.repository

import android.os.Build.PRODUCT


import com.google.firebase.firestore.FirebaseFirestore
import uz.seppuku.vp.model.Product
import uz.seppuku.vp.utils.Logger
import uz.seppuku.vp.utils.NetworkResponse
import javax.inject.Inject

class ProductsRepositoryImpl @Inject constructor(private val database: FirebaseFirestore) {

    val TAG = "ProductsRepositoryImpl"

}