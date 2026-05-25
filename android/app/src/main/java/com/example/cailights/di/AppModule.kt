package com.example.cailights.di

import com.example.cailights.data.auth.FakeAuthRepository
import com.example.cailights.data.feed.FakeFeedRepository
import com.example.cailights.data.messages.FakeMessagesRepository
import com.example.cailights.domain.auth.AuthRepository
import com.example.cailights.domain.feed.FeedRepository
import com.example.cailights.domain.messages.MessagesRepository
import com.example.cailights.ui.FeedViewModel
import com.example.cailights.ui.MessagesViewModel
import com.example.cailights.ui.SignInViewModel
import com.example.cailights.ui.SignUpViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val appModule = module {
    single<AuthRepository> { FakeAuthRepository(androidContext()) }
    singleOf(::FakeFeedRepository) { bind<FeedRepository>() }
    singleOf(::FakeMessagesRepository) { bind<MessagesRepository>() }
    
    viewModelOf(::SignInViewModel)
    viewModelOf(::SignUpViewModel)
    viewModelOf(::FeedViewModel)
    viewModelOf(::MessagesViewModel)
}
