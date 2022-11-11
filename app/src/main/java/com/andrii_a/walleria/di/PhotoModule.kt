package com.andrii_a.walleria.di

import com.andrii_a.walleria.data.remote.repository.PhotoRepositoryImpl
import com.andrii_a.walleria.data.remote.source.photo.PhotoService
import com.andrii_a.walleria.data.util.BASE_API_URL
import com.andrii_a.walleria.domain.repository.PhotoRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
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

}