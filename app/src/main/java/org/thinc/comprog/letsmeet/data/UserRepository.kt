package org.thinc.comprog.letsmeet.data

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import durdinapps.rxfirebase2.RxFirestore
import io.reactivex.Observable

class UserRepository(firebaseFirestore: FirebaseFirestore) {

    private val userRef = firebaseFirestore.collection("users")
    fun getCurrentUser(id: String): Observable<DocumentSnapshot> {
        val ref = userRef.document(id)
        return RxFirestore.observeDocumentRef(ref).toObservable().doOnError {
            
        }
    }

}
