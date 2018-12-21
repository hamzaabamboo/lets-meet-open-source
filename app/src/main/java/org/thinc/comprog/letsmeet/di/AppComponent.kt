package org.thinc.comprog.letsmeet.di

import android.app.Application
import dagger.BindsInstance
import dagger.Component
import dagger.android.support.AndroidSupportInjectionModule
import org.thinc.comprog.letsmeet.App
import org.thinc.comprog.letsmeet.data.di.RepositoryModule
import org.thinc.comprog.letsmeet.data.source.di.FirebaseModule
import javax.inject.Singleton

interface ComponentBuilder<out C> {
    fun build(): C
}

@Singleton
@Component(
    modules = [
        AndroidSupportInjectionModule::class,
        AppModule::class,
        ActivityBuilder::class,
        RepositoryModule::class,
        FirebaseModule::class
    ]
)
interface AppComponent {

    fun inject(app: App)

    @Component.Builder
    interface Builder : ComponentBuilder<AppComponent> {

        @BindsInstance
        fun application(application: Application): Builder
    }

}