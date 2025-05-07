package com.andrii_a.walleria.di

import com.andrii_a.walleria.data.remote.repository.PhotoRepositoryImpl
import com.andrii_a.walleria.data.remote.services.AndroidPhotoDownloader
import com.andrii_a.walleria.data.remote.services.PhotoService
import com.andrii_a.walleria.data.remote.services.PhotoServiceImpl
import com.andrii_a.walleria.domain.repository.PhotoRepository
import com.andrii_a.walleria.domain.services.PhotoDownloader
import com.andrii_a.walleria.ui.photo_details.PhotoDetailsViewModel
import com.andrii_a.walleria.ui.photos.PhotosViewModel
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val photosModule = module {
    singleOf(::PhotoServiceImpl) { bind<PhotoService>() }
    singleOf(::PhotoRepositoryImpl) { bind<PhotoRepository>() }
    factoryOf(::AndroidPhotoDownloader) { bind<PhotoDownloader>() }

    viewModelOf(::PhotosViewModel)
    viewModelOf(::PhotoDetailsViewModel)
}
