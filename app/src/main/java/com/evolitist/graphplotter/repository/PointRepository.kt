package com.evolitist.graphplotter.repository

import android.annotation.SuppressLint
import android.os.Build
import android.util.Log
import com.evolitist.graphplotter.model.ResponseOuter
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.ConnectionSpec
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.create
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST
import java.security.KeyStore
import java.security.cert.X509Certificate
import javax.net.ssl.*

object PointRepository {
    private const val TAG = "Repository"

    private val moshi by lazy { Moshi.Builder().add(KotlinJsonAdapterFactory()).build() }

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .apply {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    Log.d(TAG, "OS version ${Build.VERSION.RELEASE}, using placeholder URL with working updated TLS")
                    Log.w(TAG, "Using original URL on this version of Android is not possible" +
                            " because \"DHE-RSA-AES128-SHA\" encryption (which is still used by your server)" +
                            " is NOT supported in Android 8.0+ on system level.")
                    Log.wtf(TAG, "Also, TLS v1.0 will reach its EOL in March 2020, yet your server still uses it." +
                            " Do you really think using outdated software is OK even on testing/demo servers?")
                    baseUrl("https://us-central1-evolitist-1337.cloudfunctions.net/")
                    client(OkHttpClient())
                } else {
                    Log.d(TAG, "OS version ${Build.VERSION.RELEASE}, using original URL")
                    baseUrl("https://demo.bankplus.ru/mobws/json/")
                    client(getUnsafeOkHttpClient())
                }
            }
            .build()
    }

    val pointsApi by lazy { retrofit.create<PointsService>() }

    interface PointsService {
        @FormUrlEncoded
        @POST("pointsList")
        suspend fun getPoints(
            @Field("count") count: Int,
            @Field("version") version: String = "1.1"
        ): ResponseOuter
    }

    private fun getUnsafeOkHttpClient() = try {
        val context = SSLContext.getInstance("TLS")
        context.init(null, arrayOf(NoopTrustManager), null)
        OkHttpClient.Builder()
            .connectionSpecs(listOf(ConnectionSpec.COMPATIBLE_TLS))
            .sslSocketFactory(context.socketFactory, NoopTrustManager)
            .hostnameVerifier { host, _ -> host == "demo.bankplus.ru" }
            .build()
    } catch (e: Exception) {
        e.printStackTrace()
        OkHttpClient()
    }
}

@SuppressLint("TrustAllX509TrustManager")
object NoopTrustManager : X509TrustManager {
    override fun checkClientTrusted(chain: Array<X509Certificate>?, authType: String?) { }
    override fun checkServerTrusted(chain: Array<X509Certificate>?, authType: String?) { }
    override fun getAcceptedIssuers() = arrayOf<X509Certificate>()
}
