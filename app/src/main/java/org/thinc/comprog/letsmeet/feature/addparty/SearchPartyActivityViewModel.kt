package org.thinc.comprog.letsmeet.feature.addparty

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import org.thinc.comprog.letsmeet.data.PartyRepository
import org.thinc.comprog.letsmeet.data.model.Party
import org.thinc.comprog.letsmeet.data.model.User

class SearchPartyActivityViewModel : ViewModel() {

    var parties = MutableLiveData<List<Party>>().apply { value = emptyList() }
    val user = MutableLiveData<User>()
    lateinit var partyRepository: PartyRepository

    var compositeDisposable = CompositeDisposable()
    fun getPublicParties() {
        compositeDisposable.add(
            partyRepository.getPublicParties().observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io()).subscribe {
                    val list = it.documents.map { doc -> doc.toObject(Party::class.java)!!.copy(id = doc.id) }
                    parties.postValue(list.filter { party -> party.id !in user.value!!.currentParties })
                }
        )
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }
}