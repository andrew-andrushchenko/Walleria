package com.andrii_a.walleria.di

import com.andrii_a.walleria.data.local.db.dao.RecentSearchesDao
import com.andrii_a.walleria.data.local.repository.RecentSearchesRepositoryImpl
import com.andrii_a.walleria.data.remote.repository.SearchRepositoryImpl
import com.andrii_a.walleria.data.remote.services.SearchService
import com.andrii_a.walleria.data.util.BASE_API_URL
import com.andrii_a.walleria.domain.repository.RecentSearchesRepository
import com.andrii_a.walleria.domain.repository.SearchRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SearchModule {

    @Provides
    @Singleton
    fun provideSearchService(retrofitBuilder: Retrofit.Builder): SearchService =
        retrofitBuilder.baseUrl(BASE_API_URL).build().create(SearchService::class.java)

    @Provides
    @Singleton
    fun provideSearchRepository(searchService: SearchService): SearchRepository =
        SearchRepositoryImpl(searchService)

    @Provides
    @Singleton
    fun provideRecentSearchesRepository(recentSearchesDao: RecentSearchesDao): RecentSearchesRepository =
        RecentSearchesRepositoryImpl(recentSearchesDao)
}