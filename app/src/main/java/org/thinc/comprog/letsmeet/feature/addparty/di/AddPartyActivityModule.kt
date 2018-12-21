package org.thinc.comprog.letsmeet.feature.addparty.di

import dagger.Module
import dagger.Provides
import org.thinc.comprog.letsmeet.feature.addparty.AddPartyActivityViewModel

@Module
class AddPartyActivityModule {
    @Provides
    fun provideViewModel() = AddPartyActivityViewModel()
}