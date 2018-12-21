package org.thinc.comprog.letsmeet.feature.addparty

import android.app.Activity
import android.content.Intent
import android.view.View
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import org.thinc.comprog.letsmeet.data.model.Party
import org.thinc.comprog.letsmeet.feature.addparty.AddPartyActivity.Companion.JOIN_PUBLIC_PARTY_SUCCESS
import org.thinc.comprog.letsmeet.feature.chat.ChatActivity
import org.thinc.comprog.letsmeet.feature.chat.PartyInfoFragment

class SearchPartyListViewModel(val party: Party) : ViewModel() {
    fun onCardClick(v: View) {
        val intent = Intent(v.context, ChatActivity::class.java).apply {
            putExtra("party", party)
        }
        (v.context as Activity).setResult(JOIN_PUBLIC_PARTY_SUCCESS, intent)
        (v.context as Activity).finish()
    }
    fun onCardClickInfo(v: View) {
        val activity = v.context as FragmentActivity
        PartyInfoFragment().newInstance(party).show(activity.supportFragmentManager, "dialog")

    }
}