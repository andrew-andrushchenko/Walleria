package com.andrii_a.walleria.di

import com.andrii_a.walleria.data.remote.repository.TopicRepositoryImpl
import com.andrii_a.walleria.data.remote.services.TopicService
import com.andrii_a.walleria.data.util.BASE_API_URL
import com.andrii_a.walleria.domain.repository.TopicRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object TopicModule {

    @Provides
    @Singleton
    fun provideTopicService(retrofitBuilder: Retrofit.Builder): TopicService =
        retrofitBuilder.baseUrl(BASE_API_URL).build().create(TopicService::class.java)

    @Provides
    @Singleton
    fun provideTopicRepository(topicService: TopicService): TopicRepository =
        TopicRepositoryImpl(topicService)

}