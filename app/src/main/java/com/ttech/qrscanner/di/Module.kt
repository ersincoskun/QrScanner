package com.ttech.qrscanner.di

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@InstallIn(SingletonComponent::class)
@Module
object Module {

   /* @Singleton
    @Provides
    fun injectRetrofitAPI(): ApiInterface {
        val okttp = OkHttpClient.Builder()
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .client(okttp.build())
            .build()
            .create(ApiInterface::class.java)
    }
*/
}