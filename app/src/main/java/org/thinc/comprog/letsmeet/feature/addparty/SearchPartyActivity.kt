package org.thinc.comprog.letsmeet.feature.addparty

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.SimpleItemAnimator
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.android.synthetic.main.activity_search_party.*
import org.thinc.comprog.letsmeet.R
import org.thinc.comprog.letsmeet.data.PartyRepository
import org.thinc.comprog.letsmeet.data.model.User
import org.thinc.comprog.letsmeet.databinding.ActivitySearchPartyBinding

import javax.inject.Inject

class SearchPartyActivity : DaggerAppCompatActivity() {

    @Inject
    lateinit var viewModel: SearchPartyActivityViewModel

    @Inject
    lateinit var partyRepository: PartyRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_party)

        val user = intent.getParcelableExtra<User>("user")
        viewModel.partyRepository = partyRepository
        viewModel.user.value = user
        viewModel.getPublicParties()

        setSupportActionBar(toolbar5)
        supportActionBar?.title = "Find public party"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        swipe_refresh_layout.isEnabled = false

        viewModel.parties.observe(this, Observer {
            if (it.isEmpty()) {
                no_public_party_message.visibility = View.VISIBLE
                search_party_list.visibility = View.GONE
            } else {
                no_public_party_message.visibility = View.GONE
                search_party_list.visibility = View.VISIBLE
            }
        })

        search_party_list.adapter = SearchPartyListAdapter(this, viewModel.parties)
        (search_party_list.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
        search_party_list.layoutManager = LinearLayoutManager(this)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
        }
        return true
    }
}