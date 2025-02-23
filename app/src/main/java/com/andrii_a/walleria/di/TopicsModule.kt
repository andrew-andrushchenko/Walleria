package com.andrii_a.walleria.di

import com.andrii_a.walleria.data.remote.repository.TopicRepositoryImpl
import com.andrii_a.walleria.data.remote.services.TopicService
import com.andrii_a.walleria.data.remote.services.TopicServiceImpl
import com.andrii_a.walleria.domain.repository.TopicRepository
import com.andrii_a.walleria.ui.topic_details.TopicDetailsViewModel
import com.andrii_a.walleria.ui.topics.TopicsViewModel
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val topicsModule = module {
    singleOf(::TopicServiceImpl) { bind<TopicService>() }
    singleOf(::TopicRepositoryImpl) { bind<TopicRepository>() }

    viewModelOf(::TopicsViewModel)
    viewModelOf(::TopicDetailsViewModel)
}
