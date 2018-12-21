package org.thinc.comprog.letsmeet.feature.chat

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.DocumentChange
import com.stfalcon.chatkit.messages.MessagesListAdapter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import org.thinc.comprog.letsmeet.common.DisposableTracker
import org.thinc.comprog.letsmeet.data.PartyRepository
import org.thinc.comprog.letsmeet.data.UserRepository
import org.thinc.comprog.letsmeet.data.model.ChatMessage
import org.thinc.comprog.letsmeet.data.model.Party
import org.thinc.comprog.letsmeet.data.model.User

class ChatActivityViewModel : ViewModel(), DisposableTracker {
    val party = MutableLiveData<Party>()
    val user = MutableLiveData<User>()
    val members = MutableLiveData<List<User>>().apply {
        value = emptyList()
    }
    lateinit var adapter: MessagesListAdapter<ChatMessage>
    override val compositeDisposable = CompositeDisposable()

    val isLeaving = MutableLiveData<Boolean>().apply { value = false }
    val isLoadingUser = MutableLiveData<Boolean>().apply { value = true }
    lateinit var partyRepository: PartyRepository
    lateinit var userRepository: UserRepository
    fun subscribe(id: String) {
        partyRepository.subscribeToParty(id).observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io()).subscribe {
                val party = it.toObject(Party::class.java)!!.copy(id = it.id)
                this.party.postValue(party)
                if ( members.value!!.isEmpty() ) getUsers(id)
            }.track()
    }

    fun getUser(uid: String) {
        compositeDisposable.add(
            userRepository.getCurrentUser(uid).subscribe {
                val user = it.toObject(User::class.java)!!.copy(id=it.id)
                this.user.postValue(user)
            }
        )
    }
    fun getUsers(partyid: String) {
        partyRepository.getPartyMembers(partyid).observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io()).subscribe {
                val members = it.map { doc -> doc.toObject(User::class.java).copy(id = doc.id) }
                this.members.postValue(members)
                isLoadingUser.postValue(false)
            }.track()
    }

    fun getChat() {
        partyRepository.getChat(party.value!!.id).observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io()).subscribe {
                it.documentChanges.forEach { change ->
                    val newMessage = change.document.toObject(ChatMessage::class.java).copy(
                        id = change.document.id,
                        senderUser = members.value?.find { usr -> usr.id == change.document.get("sender").toString() }
                            ?: User())
                    when (change.type) {
                        DocumentChange.Type.ADDED -> adapter.addToStart(newMessage, true)
                        DocumentChange.Type.MODIFIED -> adapter.update(newMessage)
                        DocumentChange.Type.REMOVED -> adapter.deleteById(change.document.id)
                    }
                }
            }.track()
    }

    fun leaveParty(ctx: Context) {
        isLeaving.value = true
        partyRepository.leaveParty(party.value!!.id).observeOn(AndroidSchedulers.mainThread()).subscribeOn(
            Schedulers.io()
        ).subscribe { it ->
            isLeaving.value = false
            (ctx as Activity).finish()
        }.track()
    }

    fun removeObservers() {
        cleanUpDisposable()
    }

    override fun onCleared() {
        super.onCleared()
        cleanUpDisposable()
    }

}