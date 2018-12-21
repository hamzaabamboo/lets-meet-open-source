package org.thinc.comprog.letsmeet.data.di

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.functions.FirebaseFunctions
import dagger.Module
import dagger.Provides
import dagger.Reusable
import org.thinc.comprog.letsmeet.data.PartyRepository
import org.thinc.comprog.letsmeet.data.UserRepository

@Module
class RepositoryModule {

    @Provides
    @Reusable
    internal fun providePartyRepository(firebaseFirestore: FirebaseFirestore, firebaseFunctions: FirebaseFunctions) =
        PartyRepository(firebaseFirestore, firebaseFunctions)

    @Provides
    @Reusable
    internal fun provideUserRepository(firebaseFirestore: FirebaseFirestore) = UserRepository(firebaseFirestore)
}