package com.chainslarp.app.application.network.airtable

import com.chainslarp.app.R
import com.chainslarp.app.application.core.app
import com.squareup.moshi.Moshi
import okhttp3.*
import org.kodein.di.Kodein
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.singleton
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

private const val AUTHORIZATION = "Authorization"
private const val AIRTABLE_BASE_URL = "https://api.airtable.com/"

object DataModule {
    fun create() = Kodein.Module("Data") {
        bind<Moshi>() with singleton { Moshi.Builder().build() }
        bind<Retrofit>() with singleton {
            Retrofit.Builder()
                .baseUrl(AIRTABLE_BASE_URL)
                .client(instance())
                .addConverterFactory(MoshiConverterFactory.create(instance()))
                .build()
        }

        bind<AirtableApi>() with singleton {
            instance<Retrofit>().create(AirtableApi::class.java)
        }

        bind<AirtableRepository>() with singleton { AirtableRepository(instance()) }
        bind<OkHttpClient>() with singleton { prepareOkHttpClient() }
    }

    private fun prepareOkHttpClient() : OkHttpClient {
        val baseOkHttpBuilder = OkHttpClient().newBuilder().authenticator(object : Authenticator {
            override fun authenticate(route: Route?, response: Response): Request? {
                val airtableApiKey = app.getString(R.string.AIRTABLE_API_KEY)
                return response.request.newBuilder()
                    .header(AUTHORIZATION, "Bearer $airtableApiKey")
                    .build()
            }
        })
        return baseOkHttpBuilder
            .build()
    }
}