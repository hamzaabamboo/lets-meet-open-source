package org.thinc.comprog.letsmeet.feature.login.di

import dagger.Module
import dagger.Provides
import org.thinc.comprog.letsmeet.feature.profile.ProfileActivityViewModel

@Module
class ProfileActivityModule {
    @Provides
    fun provideViewModel() = ProfileActivityViewModel()
}