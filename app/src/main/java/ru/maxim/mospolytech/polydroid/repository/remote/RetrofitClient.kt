package ru.maxim.mospolytech.polydroid.repository.remote

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import androidx.core.net.toUri
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.logging.HttpLoggingInterceptor.Level.*
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File

/**
 * Retrofit client singleton object
 * @property context sets from [ru.maxim.mospolytech.polydroid.App] class
 */
object RetrofitClient {

    lateinit var context: Context
    private const val baseUrl = "https://polydroid-api.herokuapp.com/"

    private val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply { level = HEADERS })
            .build()
    }

    val instance: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
    }

    /**
     * Shows is device has internet connection
     */
    private fun isOnline(): Boolean {
        val connMgr = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo: NetworkInfo? = connMgr.activeNetworkInfo
        return networkInfo?.isConnected?:false
    }
}
