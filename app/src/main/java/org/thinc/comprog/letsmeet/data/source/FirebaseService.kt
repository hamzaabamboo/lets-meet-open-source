package org.thinc.comprog.letsmeet.data.source

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.iid.FirebaseInstanceId
import durdinapps.rxfirebase2.RxFirestore
import io.reactivex.Observable

class FirebaseService(
    val firestore: FirebaseFirestore,
    val functions: FirebaseFunctions,
    val firebaseInstanceId: FirebaseInstanceId
) {

    //Obligatory hello world methods
    fun getHelloWorld(): Observable<DocumentSnapshot> {
        val ref = firestore.collection("hello").document("world")
        return RxFirestore.observeDocumentRef(ref).toObservable()
    }

    fun subscribeHelloWorld(): Observable<DocumentSnapshot> {
        val ref = firestore.collection("hello").document("subscribe")
        return RxFirestore.observeDocumentRef(ref).toObservable()
    }

    fun sendInstanceId() {
        firebaseInstanceId.instanceId.addOnCompleteListener {
            val instanceId = it.result!!.id
            val token = it.result!!.token
            functions.getHttpsCallable("saveInstanceId").call(hashMapOf("id" to instanceId))
            functions.getHttpsCallable("saveToken").call(hashMapOf("token" to token))
        }
    }
}