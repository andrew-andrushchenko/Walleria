package com.andrii_a.walleria.di

import com.andrii_a.walleria.data.local.repository.LocalPreferencesRepositoryImpl
import com.andrii_a.walleria.data.local.repository.LocalAccountRepositoryImpl
import com.andrii_a.walleria.domain.repository.LocalPreferencesRepository
import com.andrii_a.walleria.domain.repository.LocalAccountRepository
import org.koin.dsl.module

val appPreferencesModule = module {
    single<LocalAccountRepository> { LocalAccountRepositoryImpl(get()) }
    single<LocalPreferencesRepository> { LocalPreferencesRepositoryImpl(get()) }
}
