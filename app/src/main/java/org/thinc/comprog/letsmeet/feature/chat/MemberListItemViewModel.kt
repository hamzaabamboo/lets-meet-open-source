package org.thinc.comprog.letsmeet.feature.chat

import android.content.Intent
import android.view.View
import androidx.lifecycle.ViewModel
import org.thinc.comprog.letsmeet.data.model.User
import org.thinc.comprog.letsmeet.feature.profile.ProfileActivity

class MemberListItemViewModel(val user: User) : ViewModel() {
    fun showProfile(view: View) {
        val context = view.context
        val i = Intent(context, ProfileActivity::class.java).apply {
            putExtra("user", user)
        }
        context.startActivity(i)
    }
}