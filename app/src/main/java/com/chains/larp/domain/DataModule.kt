package com.chains.larp.domain

import com.chains.larp.BuildConfig
import com.minikorp.grove.Grove
import com.squareup.moshi.Moshi
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.instance
import org.kodein.di.singleton
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

private const val AUTHORIZATION = "Authorization"
private const val AIRTABLE_BASE_URL = "https://api.airtable.com/"
private const val AIRTABLE_API_KEY = "keyqPFYrbV1XdgN3e"

object DataModule {
    fun create() = DI.Module("DataModule") {
        bind<Moshi>() with singleton { Moshi.Builder().build() }
        bind<Retrofit>() with singleton {
            Retrofit.Builder()
                .client(instance())
                .baseUrl(AIRTABLE_BASE_URL)
                .addConverterFactory(MoshiConverterFactory.create(instance()))
                .build()
        }

        bind<AirtableApi>() with singleton {
            instance<Retrofit>()
                .create(AirtableApi::class.java)
        }

        bind<OkHttpClient>() with singleton { prepareOkHttpClient() }
        bind<AirtableRepository>() with singleton { AirtableRepository(instance()) }
    }

    private fun prepareOkHttpClient(): OkHttpClient {
        val interceptor = HttpLoggingInterceptor(object : HttpLoggingInterceptor.Logger {
            override fun log(message: String) {
                Grove.d{message}
            }
        }).setLevel(if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY
        else HttpLoggingInterceptor.Level.NONE)

        val baseOkHttpBuilder = OkHttpClient().newBuilder().authenticator(object : Authenticator {
            override fun authenticate(route: Route?, response: Response): Request {
                val airtableApiKey = AIRTABLE_API_KEY
                return response.request.newBuilder()
                    .header(AUTHORIZATION, "Bearer $airtableApiKey")
                    .build()
            }
        })
        return baseOkHttpBuilder
            .addInterceptor(interceptor)
            .build()
    }
}
