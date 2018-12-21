package org.thinc.comprog.letsmeet.data

import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.functions.FirebaseFunctions
import com.google.gson.Gson
import durdinapps.rxfirebase2.RxFirestore
import io.reactivex.Observable
import io.reactivex.Single
import org.thinc.comprog.letsmeet.data.model.*
import org.w3c.dom.Document
import java.text.SimpleDateFormat
import java.util.*

class PartyRepository(
    firebaseFirestore: FirebaseFirestore,
    private val firebaseFunctions: FirebaseFunctions
) {

    private val gson = Gson()
    private val partyRef = firebaseFirestore.collection("parties")
    private val userRef = firebaseFirestore.collection("users")

    fun createParty(party: CreateParty): Single<Party>? {
        return Single.create<Party> { emitter ->
            firebaseFunctions.getHttpsCallable("createParty").call(party.toMap()).continueWith {
                val result = it.result?.data.toString()
                val createdParty = gson.fromJson<Party>(result, Party::class.java)
                emitter.onSuccess(createdParty)
            }
        }
    }

    fun editParty(party: EditParty): Single<Party>? {
        return Single.create<Party> { emitter ->
            firebaseFunctions.getHttpsCallable("editParty").call(party.toMap()).continueWith {
                val result = it.result?.data.toString()
                val createdParty = gson.fromJson<Party>(result, Party::class.java)
                emitter.onSuccess(createdParty)
            }
        }
    }

    fun getJoinedParties(uid: String): Observable<QuerySnapshot> {
        return RxFirestore.observeQueryRef(partyRef.whereArrayContains("members", uid)).toObservable()
    }

    fun subscribeToParty(id: String): Observable<DocumentSnapshot> {
        return RxFirestore.observeDocumentRef(partyRef.document(id)).toObservable()
    }

    fun joinParty(party: String): Observable<Party> {
        return Observable.create<Party> { emitter ->
            firebaseFunctions.getHttpsCallable("joinParty").call(hashMapOf("party" to party))
                .continueWith {
                    val result = it.result?.data.toString()
                    val joinedParty = gson.fromJson<Party>(result, Party::class.java)
                    emitter.onNext(joinedParty)
                }
        }.doOnError {

        }
    }

    fun getPartyMembers(party: String): Observable<QuerySnapshot> {
        return RxFirestore.observeQueryRef(userRef.whereArrayContains("currentParties", party)).toObservable()
    }

    fun getPublicParties(): Observable<QuerySnapshot> {
        return RxFirestore.observeQueryRef(partyRef.whereEqualTo("public", true)).toObservable()
    }

    fun getChat(party: String): Observable<QuerySnapshot> {
        return RxFirestore.observeQueryRef(partyRef.document(party).collection("messages").orderBy("time"))
            .toObservable()
    }

    fun sendMessage(content: String, party: String, sender: User) : Observable<DocumentReference> {
        val time = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss+00:00", Locale.US).apply {
            timeZone = TimeZone.getTimeZone("UTC")
        }.format(Calendar.getInstance().time)
        return RxFirestore.addDocument(partyRef.document(party).collection("messages"),
            hashMapOf("content" to content, "time" to time, "sender" to sender.id) as Map<String, Any>
        ).toObservable()
    }

    fun leaveParty(party: String): Single<Boolean> {
        return Single.create<Boolean> { emitter ->
            firebaseFunctions.getHttpsCallable("leaveParty").call(hashMapOf("party" to party)).continueWith {
                emitter.onSuccess(true)
            }
        }
    }
}
