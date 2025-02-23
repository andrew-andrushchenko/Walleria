package com.andrii_a.walleria.di

import com.andrii_a.walleria.data.remote.repository.CollectionRepositoryImpl
import com.andrii_a.walleria.data.remote.services.CollectionsService
import com.andrii_a.walleria.data.remote.services.CollectionsServiceImpl
import com.andrii_a.walleria.domain.repository.CollectionRepository
import com.andrii_a.walleria.ui.collect_photo.CollectPhotoViewModel
import com.andrii_a.walleria.ui.collection_details.CollectionDetailsViewModel
import com.andrii_a.walleria.ui.collections.CollectionsViewModel
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val collectionsModule = module {
    singleOf(::CollectionsServiceImpl) { bind<CollectionsService>() }

    singleOf(::CollectionRepositoryImpl) { bind<CollectionRepository>() }

    viewModelOf(::CollectionsViewModel)
    viewModelOf(::CollectionDetailsViewModel)
    viewModelOf(::CollectPhotoViewModel)
}
