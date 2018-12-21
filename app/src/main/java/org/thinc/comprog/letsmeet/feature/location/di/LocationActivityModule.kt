package org.thinc.comprog.letsmeet.feature.location.di

import dagger.Module
import dagger.Provides
import org.thinc.comprog.letsmeet.feature.location.LocationActivityViewModel

@Module
class LocationActivityModule {
    @Provides
    fun provideViewModel() = LocationActivityViewModel()
}