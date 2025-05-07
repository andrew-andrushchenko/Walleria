package com.andrii_a.walleria.di

import com.andrii_a.walleria.data.local.repository.RecentSearchesRepositoryImpl
import com.andrii_a.walleria.data.remote.repository.SearchRepositoryImpl
import com.andrii_a.walleria.data.remote.services.SearchService
import com.andrii_a.walleria.data.remote.services.SearchServiceImpl
import com.andrii_a.walleria.domain.repository.RecentSearchesRepository
import com.andrii_a.walleria.domain.repository.SearchRepository
import com.andrii_a.walleria.ui.search.SearchViewModel
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val searchModule = module {
    factoryOf(::SearchServiceImpl) { bind<SearchService>() }
    factoryOf(::SearchRepositoryImpl) { bind<SearchRepository>() }
    factoryOf(::RecentSearchesRepositoryImpl) { bind<RecentSearchesRepository>() }

    viewModelOf(::SearchViewModel)
}
