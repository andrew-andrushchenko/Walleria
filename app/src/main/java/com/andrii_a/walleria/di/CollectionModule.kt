package com.andrii_a.walleria.di

import com.andrii_a.walleria.data.remote.repository.CollectionRepositoryImpl
import com.andrii_a.walleria.data.remote.services.CollectionsService
import com.andrii_a.walleria.data.remote.services.CollectionsServiceImpl
import com.andrii_a.walleria.domain.repository.CollectionRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CollectionModule {

    /*@Provides
    @Singleton
    fun provideCollectionService(retrofitBuilder: Retrofit.Builder): CollectionsService =
        retrofitBuilder.baseUrl(BASE_API_URL).build().create(CollectionsService::class.java)*/

    @Provides
    @Singleton
    fun provideCollectionService(httpClient: HttpClient): CollectionsService = CollectionsServiceImpl(httpClient)

    @Provides
    @Singleton
    fun provideCollectionRepository(
        collectionsService: CollectionsService,
    ): CollectionRepository = CollectionRepositoryImpl(collectionsService)

}