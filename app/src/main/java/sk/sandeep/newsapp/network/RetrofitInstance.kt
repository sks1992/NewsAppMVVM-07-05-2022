package sk.sandeep.newsapp.network

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import sk.sandeep.newsapp.util_or_constants.Constants.Companion.BASE_URL

class RetrofitInstance {
    companion object {

        private val retrofit by lazy {
            val logging = HttpLoggingInterceptor()
            logging.level = HttpLoggingInterceptor.Level.BODY

            val client = OkHttpClient.Builder()
                .addInterceptor(logging)
                .build()
            /**
             * Use the Retrofit builder to build a retrofit object using a Gson converter
             */
            Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()
        }

        /**
         * A public Api object that exposes the lazy-initialized Retrofit service
         */
        val newsApi by lazy {
            retrofit.create(NewsApiService::class.java)
        }
    }
}