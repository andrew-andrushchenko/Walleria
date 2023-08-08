package com.andrii_a.walleria.di

import android.content.Context
import com.andrii_a.walleria.data.remote.repository.PhotoRepositoryImpl
import com.andrii_a.walleria.data.remote.services.AndroidPhotoDownloader
import com.andrii_a.walleria.data.remote.services.PhotoService
import com.andrii_a.walleria.data.util.BASE_API_URL
import com.andrii_a.walleria.domain.repository.PhotoRepository
import com.andrii_a.walleria.domain.services.PhotoDownloader
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object PhotoModule {

    @Provides
    @Singleton
    fun providePhotoService(retrofitBuilder: Retrofit.Builder): PhotoService =
        retrofitBuilder.baseUrl(BASE_API_URL).build().create(PhotoService::class.java)

    @Provides
    @Singleton
    fun providePhotoRepository(photoService: PhotoService): PhotoRepository =
        PhotoRepositoryImpl(photoService)

    @Provides
    @Singleton
    fun providePhotoDownloader(@ApplicationContext context: Context): PhotoDownloader =
        AndroidPhotoDownloader(context)

}