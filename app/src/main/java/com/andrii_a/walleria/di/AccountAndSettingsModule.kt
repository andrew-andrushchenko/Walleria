package com.andrii_a.walleria.di

import com.andrii_a.walleria.ui.account.AccountViewModel
import com.andrii_a.walleria.ui.profile_edit.EditUserProfileViewModel
import com.andrii_a.walleria.ui.settings.SettingsViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val accountAndSettingsModule = module {
    viewModelOf(::AccountViewModel)
    viewModelOf(::EditUserProfileViewModel)
    viewModelOf(::SettingsViewModel)
}