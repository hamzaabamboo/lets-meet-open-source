package org.thinc.comprog.letsmeet.feature.chat

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.SimpleItemAnimator
import com.afollestad.materialdialogs.MaterialDialog
import com.squareup.picasso.Picasso
import com.stfalcon.chatkit.commons.ImageLoader
import com.stfalcon.chatkit.messages.MessageHolders
import com.stfalcon.chatkit.messages.MessagesListAdapter
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.android.synthetic.main.activity_chat.*
import org.thinc.comprog.letsmeet.R
import org.thinc.comprog.letsmeet.data.PartyRepository
import org.thinc.comprog.letsmeet.data.model.ChatMessage
import org.thinc.comprog.letsmeet.databinding.ActivityChatBinding
import org.thinc.comprog.letsmeet.feature.location.LocationActivity
import javax.inject.Inject
import org.thinc.comprog.letsmeet.R.id.imageView
import androidx.core.view.ViewCompat.setTransitionName
import android.os.Build
import android.transition.TransitionInflater
import android.util.Log
import androidx.core.view.ViewCompat
import org.thinc.comprog.letsmeet.data.model.EditParty
import org.thinc.comprog.letsmeet.feature.createparty.EditPartyActivity
import org.thinc.comprog.letsmeet.feature.createparty.EditPartyActivity_MembersInjector
import com.google.firebase.auth.FirebaseAuth
import org.thinc.comprog.letsmeet.data.UserRepository
import org.thinc.comprog.letsmeet.data.model.User


class ChatActivity : DaggerAppCompatActivity() {

    @Inject
    lateinit var viewModel: ChatActivityViewModel
    @Inject
    lateinit var partyRepository: PartyRepository
    @Inject
    lateinit var userRepository: UserRepository
    lateinit var binding: ActivityChatBinding
    lateinit var toggle: ActionBarDrawerToggle

    lateinit var leavingDialog: MaterialDialog

    @SuppressLint("NewApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            party_topic.transitionName = intent.getStringExtra("titleTransition")
        }
        window.sharedElementEnterTransition = TransitionInflater.from(this).inflateTransition(R.transition.title_text)
        viewModel.partyRepository = partyRepository
        viewModel.userRepository = userRepository
        if (intent.hasExtra("user")) {
            viewModel.user.value = intent.getParcelableExtra("user")
            viewModel.party.value = intent.getParcelableExtra("party")
            viewModel.subscribe(viewModel.party.value!!.id)
        } else {
            val auth = FirebaseAuth.getInstance()
            if ( auth.currentUser == null ) {
                finish()
            } else {
                var uid = auth.currentUser?.uid!!
                viewModel.getUser(uid)
                var partyId = intent.getStringExtra("partyId")
                viewModel.subscribe(partyId)
            }
        }

        // Data Binding Stuff
        binding = DataBindingUtil.setContentView(this, R.layout.activity_chat)
        binding.executePendingBindings()
        binding.viewModel = viewModel
        binding.setLifecycleOwner(this)

        toggle = ActionBarDrawerToggle(this, drawer_layout, binding.toolbar, R.string.Open, R.string.Close)
        drawer_layout.addDrawerListener(toggle)

        setSupportActionBar(binding.toolbar)

        // supportActionBar!!.title = viewModel.party.value!!.topic
        supportActionBar!!.title = null
        binding.toolbar.navigationIcon = null
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toggle.isDrawerIndicatorEnabled = false
        supportActionBar!!.setHomeButtonEnabled(true)

        toggle.syncState()

        val config = MessageHolders()
        config.setIncomingTextLayout(R.layout.custom_incoming_text_message)

        val adapter =
            MessagesListAdapter<ChatMessage>(viewModel.user.value?.id, config, ImageLoader { imageView, url, payload ->
                Picasso.get().load(url).into(imageView)
            })
        viewModel.adapter = adapter

        messagesList.setAdapter(viewModel.adapter)
        messagesList.addOnLayoutChangeListener { _, _, _, _, bottom, _, _, _, oldBottom ->
            messagesList.smoothScrollToPosition(0)
//            if (bottom < oldBottom) {
//                val scrollPos = 2 * messagesList.height - messagesList.y - input.height
//                messagesList.smoothScrollBy(0, Math.round(scrollPos))
//            } else if (bottom > oldBottom) {
//                val scrollPos = messagesList.y + input.height //not done krub
//                messagesList.smoothScrollBy(0, Math.round(scrollPos))
//            }
        }

        viewModel.isLoadingUser.observe(this, Observer {
            if (!it) {
                viewModel.getChat()
            }
        })
        leavingDialog = MaterialDialog(this).message(text = "Leaving Party")
        viewModel.isLeaving.observe(this, Observer {
            if (it) {
                leavingDialog.show {
                    cancelable(false)  // calls setCancelable on the underlying dialog
                    cancelOnTouchOutside(false)  // calls setCanceledOnTouchOutside on the underlying dialog
                }
            } else {
                leavingDialog.hide()
            }
        })
        input.setInputListener {
            partyRepository.sendMessage(it.toString(), viewModel.party.value!!.id, viewModel.user.value!!).subscribe()
            true
        }
        // Member list drawer
        val resId = resources.getIdentifier("status_bar_height", "dimen", "android")
        val padding = resources.getDimensionPixelSize(resId)

        right_drawer.setPadding(0, padding, 0, 0)
        right_drawer.adapter = MemberListAdapter(this, viewModel.members)
        (right_drawer.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
        right_drawer.layoutManager = LinearLayoutManager(this)
    }

    override fun onSaveInstanceState(outState: Bundle?, outPersistentState: PersistableBundle?) {
        super.onSaveInstanceState(outState, outPersistentState)
        outState?.putParcelable("user", viewModel.user.value)
        outState?.putParcelable("party", viewModel.party.value)
    }

    override fun onDestroy() {
        viewModel.removeObservers()
        super.onDestroy()
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item!!.itemId) {
            R.id.toggle_drawer -> {
                drawer_layout.openDrawer(GravityCompat.END)
                return true
            }
            R.id.qr_code -> {
                val intent = Intent(this, ShowQRCodeActivity::class.java).apply {
                    putExtra("uri", "http://letsmeet.ham-san.net/join/${viewModel.party.value!!.code}")
                }
                startActivity(intent)
                return true
            }
            R.id.action_location -> {
                val intent = Intent(this, LocationActivity::class.java).apply {
                    putExtra("location", viewModel.party.value!!.location)
                }
                startActivity(intent)
                return true
            }
            R.id.action_leave -> {
                viewModel.leaveParty(this)
            }
            R.id.action_share -> {
                val sendIntent: Intent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_SUBJECT, "Sharing URL")
                    putExtra(Intent.EXTRA_TEXT, "Join ${viewModel.party.value!!.topic} now !! http://letsmeet.ham-san.net/join/${viewModel.party.value!!.code}")
                    type = "text/plain"
                }
                startActivity(Intent.createChooser(sendIntent, "Share URL"))
            }
            R.id.action_information -> {
                PartyInfoFragment().newInstance(viewModel.party.value!!).show(supportFragmentManager, "dialog")
            }
            R.id.action_edit -> {
                val intent = Intent(this, EditPartyActivity::class.java).apply {
                    putExtra("party", viewModel.party.value)
                }
                startActivity(intent)
            }

        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.member_list_toolbar, menu)
        return true
    }
}