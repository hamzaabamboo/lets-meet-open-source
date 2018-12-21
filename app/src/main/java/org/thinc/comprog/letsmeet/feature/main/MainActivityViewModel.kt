package org.thinc.comprog.letsmeet.feature.main

import android.app.Activity
import android.content.Intent
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.firebase.ui.auth.AuthUI
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import org.thinc.comprog.letsmeet.data.PartyRepository
import org.thinc.comprog.letsmeet.data.UserRepository
import org.thinc.comprog.letsmeet.data.model.Party
import org.thinc.comprog.letsmeet.data.model.User
import org.thinc.comprog.letsmeet.feature.addparty.AddPartyActivity
import org.thinc.comprog.letsmeet.feature.profile.ProfileActivity
import android.app.AlarmManager
import android.content.Context.ALARM_SERVICE
import androidx.core.content.ContextCompat.getSystemService
import android.app.PendingIntent
import android.content.Context


class MainActivityViewModel : ViewModel() {
    lateinit var partyRepository: PartyRepository
    lateinit var userRepository: UserRepository

    lateinit var activity : MainActivity
    var isRefreshing = MutableLiveData<Boolean>().apply { value = false }
    var isLoadingUser = MutableLiveData<Boolean>().apply { value = false }
    private val compositeDisposable = CompositeDisposable()

    val parties = MutableLiveData<List<Party>>().apply { value = emptyList() }

    val user = MutableLiveData<User>()

    fun getCurrentUser(uid: String) {
        isLoadingUser.value = true
        compositeDisposable.add(
            userRepository.getCurrentUser(uid).observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io()).subscribe {
                    val mUser = it.toObject(User::class.java)!!.copy(id = it.id)
                    isLoadingUser.postValue(false)
                    user.postValue(mUser)
                }
        )
    }

    fun getRooms(uid: String) {
        compositeDisposable.add(
            partyRepository.getJoinedParties(uid).observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io()).subscribe {
                    val list = it.documents.map { doc -> doc.toObject(Party::class.java)!!.copy(id = doc.id) }
                    parties.postValue(list)
                }
        )
    }

    fun logout(v : View) {
        removeObservers()
        activity.finish()
        AuthUI.getInstance()
            .signOut(activity)
            .addOnCompleteListener {
                val intent = Intent(activity, MainActivity::class.java)
                activity.startActivity(intent)
            }
    }

    fun toAdd(v: View) {
        val context = v.context
        val intent = Intent(context, AddPartyActivity::class.java).apply {
            this.putExtra("user", user.value)
        }
        context.startActivity(intent)
    }

    fun removeObservers() {
        compositeDisposable.dispose()
    }

    fun showProfile(v: View) {
        val intent = Intent(activity, ProfileActivity::class.java).apply {
            putExtra("user", user.value!!)
        }
        activity.startActivity(intent)
    }
    override fun onCleared() {
        super.onCleared()
        removeObservers()
    }
}
