package org.thinc.comprog.letsmeet.feature.addparty

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.disposables.CompositeDisposable
import org.thinc.comprog.letsmeet.data.PartyRepository
import org.thinc.comprog.letsmeet.data.model.User
import org.thinc.comprog.letsmeet.feature.addparty.AddPartyActivity.Companion.ADD_PARTY_QR
import org.thinc.comprog.letsmeet.feature.addparty.AddPartyActivity.Companion.CREATE_PARTY
import org.thinc.comprog.letsmeet.feature.addparty.AddPartyActivity.Companion.JOIN_PUBLIC_PARTY
import org.thinc.comprog.letsmeet.feature.chat.ChatActivity
import org.thinc.comprog.letsmeet.feature.createparty.CreatePartyActivity


class AddPartyActivityViewModel : ViewModel() {

    val user = MutableLiveData<User>()
    val code = MutableLiveData<String>().apply { value = "" }
    lateinit var partyRepository: PartyRepository

    val isJoiningParty = MutableLiveData<Boolean>().apply { value = false }
    private val compositeDisposable: CompositeDisposable = CompositeDisposable()

    fun byCode(view: View) {
        val context = view.context
        isJoiningParty.value = true
        compositeDisposable.add(
            partyRepository.joinParty(code.value!!).subscribe {
                val intent = Intent(context, ChatActivity::class.java).apply {
                    putExtra("party", it)
                    putExtra("user", user.value)
                }
                isJoiningParty.postValue(false)
                (context as Activity).finish()
                context.startActivity(intent)
            }
        )
    }

    fun byCamera(view: View) {
        val context = view.context
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
            == PackageManager.PERMISSION_DENIED
        ) {
            ActivityCompat.requestPermissions(
                context as Activity, arrayOf(Manifest.permission.CAMERA),
                AddPartyActivity.ADD_PARTY_QR_SUCCESS
            )

        } else {
            val intent = Intent(context, QRCodeActivity::class.java)
            (context as Activity).startActivityForResult(
                intent, ADD_PARTY_QR
            )
        }
    }

    fun toCreate(view: View) {
        val context = view.context
        val intent = Intent(context, CreatePartyActivity::class.java).apply {
            putExtra("user", user.value)
        }
        (context as Activity).startActivityForResult(
            intent, CREATE_PARTY
        )
    }

    fun toJoinPublic(view: View) {
        val context = view.context
        val intent = Intent(context, SearchPartyActivity::class.java).apply {
            putExtra("user", user.value)
        }
        (context as Activity).startActivityForResult(
            intent, JOIN_PUBLIC_PARTY
        )
    }

}