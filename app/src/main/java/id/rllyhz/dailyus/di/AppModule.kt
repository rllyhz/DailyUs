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
import id.rllyhz.dailyus.data.source.local.db.DailyStoriesDatabase
import id.rllyhz.dailyus.data.source.local.db.StoriesDao
import id.rllyhz.dailyus.data.source.local.db.StoryKeysDao
import id.rllyhz.dailyus.data.source.remote.network.DailyUsAuthApiService
import id.rllyhz.dailyus.data.source.remote.network.DailyUsStoriesApiService
import id.rllyhz.dailyus.utils.Constants
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
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
    fun provideApiClient(): OkHttpClient =
        OkHttpClient.Builder().apply {
            val loggingInterceptor = HttpLoggingInterceptor()
                .apply {
                    if (BuildConfig.DEBUG) setLevel(HttpLoggingInterceptor.Level.BODY)
                    else setLevel(HttpLoggingInterceptor.Level.NONE)
                }

            addInterceptor(loggingInterceptor)
        }
            .connectTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()

    @Provides
    @Singleton
    fun provideAuthApiService(
        apiClient: OkHttpClient
    ): DailyUsAuthApiService {
        val retrofit = Retrofit.Builder()
            .baseUrl(Constants.apiBaseUrl)
            .addConverterFactory(GsonConverterFactory.create())
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
            .baseUrl(Constants.apiBaseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .client(apiClient)
            .build()

        return retrofit.create(DailyUsStoriesApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideDailyStoriesDatabase(
        @ApplicationContext context: Context
    ): DailyStoriesDatabase =
        Room.databaseBuilder(
            context,
            DailyStoriesDatabase::class.java,
            Constants.databaseName
        )
            .build()

    @Provides
    @Singleton
    fun provideStoriesDao(
        storiesDB: DailyStoriesDatabase
    ): StoriesDao = storiesDB.getStoriesDao()

    @Provides
    @Singleton
    fun provideStoryKeyDao(
        storiesDB: DailyStoriesDatabase
    ): StoryKeysDao = storiesDB.getStoryKeysDao()

    @Provides
    @Singleton
    fun provideAuthRepository(
        authApi: DailyUsAuthApiService
    ): AuthRepository = AuthRepositoryImpl(authApi)

    @Provides
    @Singleton
    fun provideDailyStoriesRepository(
        storiesApi: DailyUsStoriesApiService,
        storiesDao: StoriesDao,
        storyKeysDao: StoryKeysDao
    ): DailyStoriesRepository = DailyStoriesRepositoryImpl(storiesApi, storiesDao, storyKeysDao)
}