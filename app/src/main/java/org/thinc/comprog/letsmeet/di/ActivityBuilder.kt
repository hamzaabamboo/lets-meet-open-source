package org.thinc.comprog.letsmeet.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import org.thinc.comprog.letsmeet.feature.addparty.AddPartyActivity
import org.thinc.comprog.letsmeet.feature.addparty.SearchPartyActivity
import org.thinc.comprog.letsmeet.feature.addparty.di.AddPartyActivityModule
import org.thinc.comprog.letsmeet.feature.addparty.di.SearchPartyActivityModule
import org.thinc.comprog.letsmeet.feature.chat.ChatActivity
import org.thinc.comprog.letsmeet.feature.chat.di.ChatActivityModule
import org.thinc.comprog.letsmeet.feature.createparty.CreatePartyActivity
import org.thinc.comprog.letsmeet.feature.createparty.EditPartyActivity
import org.thinc.comprog.letsmeet.feature.createparty.di.CreatePartyActivityModule
import org.thinc.comprog.letsmeet.feature.location.LocationActivity
import org.thinc.comprog.letsmeet.feature.location.di.LocationActivityModule
import org.thinc.comprog.letsmeet.feature.login.LoginActivity
import org.thinc.comprog.letsmeet.feature.login.di.LoginActivityModule
import org.thinc.comprog.letsmeet.feature.login.di.ProfileActivityModule
import org.thinc.comprog.letsmeet.feature.main.MainActivity
import org.thinc.comprog.letsmeet.feature.main.di.MainActivityModule
import org.thinc.comprog.letsmeet.feature.profile.ProfileActivity


@Module
abstract class ActivityBuilder {

    @ContributesAndroidInjector(modules = [MainActivityModule::class])
    abstract fun bindMainActivity(): MainActivity

    @ContributesAndroidInjector(modules = [LoginActivityModule::class])
    abstract fun bindLoginActivity(): LoginActivity

    @ContributesAndroidInjector(modules = [AddPartyActivityModule::class])
    abstract fun bindAddPartyActivity(): AddPartyActivity

    @ContributesAndroidInjector(modules = [ChatActivityModule::class])
    abstract fun bindChatActivity(): ChatActivity

    @ContributesAndroidInjector(modules = [LocationActivityModule::class])
    abstract fun bindLocationActivity(): LocationActivity

    @ContributesAndroidInjector(modules = [CreatePartyActivityModule::class])
    abstract fun bindCreatePartyActivity(): CreatePartyActivity

    @ContributesAndroidInjector(modules = [CreatePartyActivityModule::class])
    abstract fun bindEditPartyActivity(): EditPartyActivity

    @ContributesAndroidInjector(modules = [SearchPartyActivityModule::class])
    abstract fun bindSearchPartyActivity(): SearchPartyActivity

    @ContributesAndroidInjector(modules = [ProfileActivityModule::class])
    abstract fun bindProfileActivity(): ProfileActivity

}