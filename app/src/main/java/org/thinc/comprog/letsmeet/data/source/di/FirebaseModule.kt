package org.thinc.comprog.letsmeet.data.source.di

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.iid.FirebaseInstanceId
import dagger.Module
import dagger.Provides
import dagger.Reusable
import org.thinc.comprog.letsmeet.data.source.FirebaseService

@Module
class FirebaseModule {

    @Provides
    @Reusable
    internal fun provideFirebaseService(
        firestore: FirebaseFirestore,
        firebaseFunctions: FirebaseFunctions,
        firebaseInstanceId: FirebaseInstanceId
    ) = FirebaseService(firestore, firebaseFunctions, firebaseInstanceId)

}