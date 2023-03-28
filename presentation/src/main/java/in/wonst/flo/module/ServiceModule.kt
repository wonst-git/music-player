package `in`.wonst.flo.module

import android.util.Log
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import `in`.wonst.data.service.SongService
import `in`.wonst.data.service.SongService.ApiConstants.BASE_URL
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ServiceModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient =
        OkHttpClient.Builder().apply {
            connectTimeout(SongService.ApiConstants.TIME_OUT, TimeUnit.SECONDS)
            writeTimeout(SongService.ApiConstants.TIME_OUT, TimeUnit.SECONDS)
            readTimeout(SongService.ApiConstants.TIME_OUT, TimeUnit.SECONDS)
            addInterceptor(HttpLoggingInterceptor(object : HttpLoggingInterceptor.Logger {
                override fun log(message: String) {
                    Log.d("SERVER_LOG", message)
                }
            }).apply { level = HttpLoggingInterceptor.Level.BODY })
        }.build()

    @Provides
    @Singleton
    fun provideRetrofit(client: OkHttpClient): Retrofit =
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()

    @Provides
    @Singleton
    fun provideSongService(retrofit: Retrofit): SongService = retrofit.create(SongService::class.java)
}