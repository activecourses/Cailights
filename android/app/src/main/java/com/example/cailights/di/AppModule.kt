package com.example.cailights.di

import com.example.cailights.ui.SignInViewModel
import com.example.cailights.ui.SignUpViewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

val appModule = module {
    viewModelOf(::SignInViewModel)
    viewModelOf(::SignUpViewModel)
}
