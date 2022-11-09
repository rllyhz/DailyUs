package id.rllyhz.dailyus.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import id.rllyhz.dailyus.BuildConfig
import id.rllyhz.dailyus.data.preferences.AuthPreferences
import id.rllyhz.dailyus.data.source.AuthRepository
import id.rllyhz.dailyus.data.source.AuthRepositoryImpl
import id.rllyhz.dailyus.data.source.DailyStoriesRepository
import id.rllyhz.dailyus.data.source.DailyStoriesRepositoryImpl
import id.rllyhz.dailyus.data.source.local.db.DailyUsDatabase
import id.rllyhz.dailyus.data.source.remote.network.DailyUsAuthApiService
import id.rllyhz.dailyus.data.source.remote.network.DailyUsStoriesApiService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideAuthPreferences(
        @ApplicationContext context: Context
    ): AuthPreferences = AuthPreferences(context)

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context
    ): DailyUsDatabase =
        Room.databaseBuilder(
            context,
            DailyUsDatabase::class.java,
            "dailyUs.db"
        ).build()

    @Provides
    @Singleton
    fun provideApiClient(): OkHttpClient =
        OkHttpClient.Builder().apply {
            val loggingInterceptor = HttpLoggingInterceptor()
                .setLevel(HttpLoggingInterceptor.Level.BODY)
                .apply { if (!BuildConfig.DEBUG) setLevel(HttpLoggingInterceptor.Level.NONE) }

            addInterceptor(loggingInterceptor)

        }.build()

    @Provides
    @Singleton
    fun provideAuthApiService(
        apiClient: OkHttpClient
    ): DailyUsAuthApiService {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://story-api.dicoding.dev/v1/")
            .addConverterFactory(MoshiConverterFactory.create())
            .client(apiClient)
            .build()

        return retrofit.create(DailyUsAuthApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideStoriesApiService(
        apiClient: OkHttpClient
    ): DailyUsStoriesApiService {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://story-api.dicoding.dev/v1/")
            .addConverterFactory(MoshiConverterFactory.create())
            .client(apiClient)
            .build()

        return retrofit.create(DailyUsStoriesApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideAuthRepository(
        authApi: DailyUsAuthApiService
    ): AuthRepository = AuthRepositoryImpl(authApi)

    @Provides
    @Singleton
    fun provideDailyStoriesRepository(
        storiesApi: DailyUsStoriesApiService,
        db: DailyUsDatabase
    ): DailyStoriesRepository = DailyStoriesRepositoryImpl(storiesApi, db)
}