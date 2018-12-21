package org.thinc.comprog.letsmeet.feature.main.di

import dagger.Module
import dagger.Provides
import org.thinc.comprog.letsmeet.data.source.FirebaseService
import org.thinc.comprog.letsmeet.feature.main.MainActivityViewModel


@Module
class MainActivityModule {
    @Provides
    fun provideViewModel() = MainActivityViewModel()

//    @Provides
//    fun providePartyListViewModel(firebaseService: FirebaseService) = PartyListViewModel(firebaseService)

}