package org.thinc.comprog.letsmeet.feature.addparty.di

import dagger.Module
import dagger.Provides
import org.thinc.comprog.letsmeet.feature.addparty.SearchPartyActivityViewModel

@Module
class SearchPartyActivityModule {
    @Provides
    fun provideViewModel() = SearchPartyActivityViewModel()
}