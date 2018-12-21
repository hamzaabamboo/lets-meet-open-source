package org.thinc.comprog.letsmeet.feature.login

import android.app.Activity
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.firebase.ui.auth.AuthUI
import org.thinc.comprog.letsmeet.R
import org.thinc.comprog.letsmeet.feature.main.MainActivity.Companion.RC_SIGN_IN

class LoginActivityViewModel : ViewModel() {
    val isLoggedIn = MutableLiveData<Boolean>().apply {
        this.value = true
    }

    fun performLogin(v: View) {
        val context = v.context
        performLogin(context as Activity)
    }

    fun performLogin(activity: Activity) {
        val providers = listOf(
            AuthUI.IdpConfig.GoogleBuilder().build(),
            AuthUI.IdpConfig.FacebookBuilder().build()
        )

        activity.startActivityForResult(
            AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setLogo(R.mipmap.ic_launcher_round)
                .setAvailableProviders(providers)
                .build(),
            RC_SIGN_IN
        )
    }

    fun setLoggedInStatus(bool: Boolean) {
        isLoggedIn.value = bool
    }
}