package org.thinc.comprog.letsmeet.feature.chat.di

import dagger.Module
import dagger.Provides
import org.thinc.comprog.letsmeet.feature.chat.ChatActivityViewModel

@Module
class ChatActivityModule {
    @Provides
    fun provideViewModel() = ChatActivityViewModel()
}