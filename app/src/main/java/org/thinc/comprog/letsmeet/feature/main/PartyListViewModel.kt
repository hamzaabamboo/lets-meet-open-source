package org.thinc.comprog.letsmeet.feature.main

import android.app.Activity
import android.content.Intent
import android.util.Log
import android.view.View
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.app.ActivityOptionsCompat
import androidx.core.view.ViewCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.thinc.comprog.letsmeet.R
import org.thinc.comprog.letsmeet.data.model.Party
import org.thinc.comprog.letsmeet.data.model.User
import org.thinc.comprog.letsmeet.feature.chat.ChatActivity
import org.thinc.comprog.letsmeet.feature.chat.PartyInfoFragment

class PartyListViewModel(val party: Party, val user: MutableLiveData<User>) : ViewModel() {
    fun onCardClick(v: View) {
        val intent = Intent(v.context, ChatActivity::class.java).apply {
            putExtra("party", party)
            putExtra("user", user.value)
            putExtra("titleTransition", party.id)
        }
        val topicView = v.findViewById<AppCompatTextView>(R.id.topic)
        val options = ActivityOptionsCompat.makeSceneTransitionAnimation(
            v.context as Activity,
            topicView,
            ViewCompat.getTransitionName(topicView)!!)
//        v.context.startActivity(intent, options.toBundle())
        v.context.startActivity(intent)
    }
    fun onCardClickInfo(v: View) {
        val activity = v.context as FragmentActivity
        PartyInfoFragment().newInstance(party).show(activity.supportFragmentManager, "dialog")

    }
}