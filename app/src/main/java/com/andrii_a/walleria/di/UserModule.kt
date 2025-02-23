package com.andrii_a.walleria.di

import com.andrii_a.walleria.data.remote.repository.UserRepositoryImpl
import com.andrii_a.walleria.data.remote.services.UserService
import com.andrii_a.walleria.data.remote.services.UserServiceImpl
import com.andrii_a.walleria.domain.repository.UserRepository
import com.andrii_a.walleria.ui.user_details.UserDetailsViewModel
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val userModule = module {
    singleOf(::UserServiceImpl) { bind<UserService>() }
    singleOf(::UserRepositoryImpl) { bind<UserRepository>() }

    viewModelOf(::UserDetailsViewModel)
}
