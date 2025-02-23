package com.andrii_a.walleria.di

import com.andrii_a.walleria.data.remote.repository.LoginRepositoryImpl
import com.andrii_a.walleria.data.remote.services.LoginService
import com.andrii_a.walleria.data.remote.services.LoginServiceImpl
import com.andrii_a.walleria.domain.repository.LoginRepository
import com.andrii_a.walleria.ui.login.LoginViewModel
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val loginModule = module {
    singleOf(::LoginServiceImpl) { bind<LoginService>() }
    singleOf(::LoginRepositoryImpl) { bind<LoginRepository>() }

    viewModelOf(::LoginViewModel)
}
