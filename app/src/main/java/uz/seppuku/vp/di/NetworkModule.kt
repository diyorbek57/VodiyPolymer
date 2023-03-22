package uz.seppuku.vp.di

import android.content.Context
import android.content.SharedPreferences
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import uz.seppuku.vp.repository.ProductsRepository
import uz.seppuku.vp.repository.ProductsRepositoryImpl
import uz.seppuku.vp.utils.Constants.LOCAL_SHARED_PREF
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideSharedPrefs(@ApplicationContext context: Context): SharedPreferences {
        return context.getSharedPreferences(LOCAL_SHARED_PREF, Context.MODE_PRIVATE)
    }
    @Singleton
    @Provides
    fun provideFirestoreDatabase(): FirebaseFirestore {
        return FirebaseFirestore.getInstance()

    }

//    @Singleton
//    @Provides
//    fun provideProductRepository(dataBase: FirebaseFirestore): ProductsRepository {
//        return ProductsRepositoryImpl(dataBase)
//    }
//
//    @Singleton
//    @Provides
//    fun provideSearchRepossitory(ktorClient: HttpClient): SearchRepository {
//        return SearchRepositoryImpl(ktorClient)
//    }
}