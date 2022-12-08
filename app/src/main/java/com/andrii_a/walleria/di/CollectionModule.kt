package com.andrii_a.walleria.di

import com.andrii_a.walleria.data.remote.repository.CollectionRepositoryImpl
import com.andrii_a.walleria.data.remote.source.collection.CollectionsService
import com.andrii_a.walleria.data.util.BASE_API_URL
import com.andrii_a.walleria.domain.repository.CollectionRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CollectionModule {

    @Provides
    @Singleton
    fun provideCollectionService(retrofitBuilder: Retrofit.Builder): CollectionsService =
        retrofitBuilder.baseUrl(BASE_API_URL).build().create(CollectionsService::class.java)

    @Provides
    @Singleton
    fun provideCollectionRepository(
        collectionsService: CollectionsService,
    ): CollectionRepository = CollectionRepositoryImpl(collectionsService)

}