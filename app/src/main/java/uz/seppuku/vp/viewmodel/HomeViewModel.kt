package uz.seppuku.vp.viewmodel


import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import uz.seppuku.vp.model.Product
import uz.seppuku.vp.utils.Logger
import uz.seppuku.vp.utils.Resource
import uz.seppuku.vp.utils.SingleLiveEvent
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val database: FirebaseFirestore
) : ViewModel() {

    val TAG = "HomeViewModel"

    //products list livedata
    private var _products = SingleLiveEvent<Resource<List<Product>>>()
    val products: LiveData<Resource<List<Product>>> get() = _products

    //single product livedata
    private var _product = SingleLiveEvent<Resource<Product>>()
    val product: LiveData<Resource<Product>> get() = _product

    //list for put products from database
    val productsList = ArrayList<Product>()

    //object for get single product and return it
    var singleProduct = Product()

    fun getAllProducts() = viewModelScope.launch(Dispatchers.IO) {
        try {
            _products.postValue(Resource.loading())
            database.collection("products").get()
                .addOnSuccessListener { documents ->
                    if (!documents.isEmpty) {
                        productsList.clear()
                        for (document in documents.documents) {
                            document.toObject(Product::class.java)!!.product_id?.let {
                                Logger.d(
                                    TAG,
                                    it
                                )
                            }
                            productsList.add(document.toObject<Product>()!!)
                        }
                        _products.postValue(Resource.success(productsList))
                    } else {
                        Logger.d(TAG, "No such document")
                    }
                }
                .addOnFailureListener { exception ->
                    Logger.e(TAG, "get failed with $exception")
                    _products.postValue(Resource.error(exception.message.toString()))
                }
        } catch (e: Exception) {
            e.localizedMessage ?: "error"
        }
    }

    fun getProductsByCategory(categoryId: String) = viewModelScope.launch(Dispatchers.IO) {
        try {
            _products.postValue(Resource.loading())
            database.collection("products").whereEqualTo("product_category_id", categoryId).get()
                .addOnSuccessListener { documents ->
                    if (!documents.isEmpty) {
                        productsList.clear()
                        for (document in documents.documents) {
                            document.toObject(Product::class.java)!!.product_id?.let {
                                Logger.d(
                                    TAG,
                                    it
                                )
                            }
                            productsList.add(document.toObject<Product>()!!)
                        }
                        _products.postValue(Resource.success(productsList))
                    } else {
                        Logger.d(TAG, "No such document")
                    }
                }
                .addOnFailureListener { exception ->
                    Logger.e(TAG, "get failed with $exception")
                    _products.postValue(Resource.error(exception.message.toString()))
                }
        } catch (e: Exception) {
            e.localizedMessage ?: "error"
        }
    }

    fun getSingleProduct(productId: String) = viewModelScope.launch(Dispatchers.IO) {
        try {
            _product.postValue(Resource.loading())
            database.collection("products").whereEqualTo("product_id", productId).get()
                .addOnSuccessListener { documents ->
                    if (!documents.isEmpty) {

                        for (document in documents.documents) {

                            singleProduct = document.toObject<Product>()!!
                        }

                        _product.postValue(Resource.success(singleProduct))
                    } else {
                        Logger.d(TAG, "No such document")
                    }
                }
                .addOnFailureListener { exception ->
                    Logger.e(TAG, "get failed with $exception")
                    _product.postValue(Resource.error(exception.message.toString()))
                }
        } catch (e: Exception) {
            e.localizedMessage ?: "error"
        }
    }
}