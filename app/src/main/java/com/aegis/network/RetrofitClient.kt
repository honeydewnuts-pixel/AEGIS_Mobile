package com.aegis.mobile.network

import android.content.Context
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.google.gson.GsonBuilder
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

private val Context.dataStore by preferencesDataStore(name = "aegis_settings")
private val SERVER_IP_KEY = stringPreferencesKey("server_ip")

object RetrofitClient {

    private const val DEFAULT_IP = "192.168.1.100" // Change this to your PC/Server IP
    private const val PORT = "5000"

    fun getApiService(context: Context): ApiService {
        val serverIp = runBlocking { 
            context.dataStore.data.first()[SERVER_IP_KEY] ?: DEFAULT_IP 
        }
        val baseUrl = "http://$serverIp:$PORT/"

        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()

        val gson = GsonBuilder().setLenient().create()

        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(ApiService::class.java)
    }
}
