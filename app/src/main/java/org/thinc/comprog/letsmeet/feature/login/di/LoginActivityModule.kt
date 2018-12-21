package org.thinc.comprog.letsmeet.feature.login.di

import dagger.Module
import dagger.Provides
import org.thinc.comprog.letsmeet.feature.login.LoginActivityViewModel

@Module
class LoginActivityModule {
    @Provides
    fun provideViewModel() = LoginActivityViewModel()
}