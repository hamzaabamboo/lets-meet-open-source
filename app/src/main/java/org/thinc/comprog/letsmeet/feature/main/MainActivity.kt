package org.thinc.comprog.letsmeet.feature.main

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.PersistableBundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.SimpleItemAnimator
import com.afollestad.materialdialogs.MaterialDialog
import com.firebase.ui.auth.AuthUI
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.firebase.auth.FirebaseAuth
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.android.synthetic.main.activity_chat.*
import kotlinx.android.synthetic.main.activity_main.*
import org.thinc.comprog.letsmeet.R
import org.thinc.comprog.letsmeet.data.PartyRepository
import org.thinc.comprog.letsmeet.data.UserRepository
import org.thinc.comprog.letsmeet.data.source.FirebaseService
import org.thinc.comprog.letsmeet.databinding.ActivityMainBinding
import org.thinc.comprog.letsmeet.feature.login.LOGIN_SUCCESS
import org.thinc.comprog.letsmeet.feature.login.LoginActivity
import javax.inject.Inject
import android.app.AlarmManager
import androidx.core.content.ContextCompat.getSystemService
import android.app.PendingIntent
import android.content.pm.PackageManager
import android.content.ComponentName
import android.content.Context
import org.joda.time.DateTime
import org.thinc.comprog.letsmeet.common.ScheduledNotificationRecevier
import java.util.*
import java.util.concurrent.ScheduledExecutorService


class MainActivity : DaggerAppCompatActivity() {

    companion object {
        const val RC_SIGN_IN = 912
        const val LOGIN = 12392
    }
    @Inject
    lateinit var viewModel: MainActivityViewModel
    @Inject
    lateinit var partyRepository: PartyRepository
    @Inject
    lateinit var userRepository: UserRepository

    @Inject
    lateinit var firebaseService: FirebaseService

    lateinit var binding: ActivityMainBinding

    private var uid: String? = null

    lateinit var loadingDialog: MaterialDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewModel.partyRepository = partyRepository
        viewModel.userRepository = userRepository
        loadingDialog = MaterialDialog(this).message(text = "Loading user information...")

        if (savedInstanceState != null) {
            viewModel.user.value = savedInstanceState.getParcelable("user")
        }
        val auth = FirebaseAuth.getInstance()
        if (auth.currentUser != null) {
            uid = auth.currentUser?.uid
            viewModel.getCurrentUser(uid!!)
            viewModel.getRooms(uid!!)
        } else {
            val intent = Intent(this, LoginActivity::class.java)
            startActivityForResult(intent, LOGIN)
        }

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.executePendingBindings()

        binding.viewModel = viewModel
        binding.setLifecycleOwner(this)

        setSupportActionBar(binding.bar)

        val offset = bar.height
        party_list.setPadding(8, 8, 8, offset + 5)
        // Party List
        party_list.adapter = PartyListAdapter(this, viewModel.parties, viewModel.user)
        (party_list.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
        party_list.layoutManager = LinearLayoutManager(this)

        viewModel.parties.observe(this, Observer {
            if (it.isEmpty()) {
                party_list.visibility = View.GONE
                no_party_message.visibility = View.VISIBLE
            } else {
                party_list.visibility = View.VISIBLE
                no_party_message.visibility = View.GONE
            }
            val am = this.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val updateBroadCastIntent = Intent(this, ScheduledNotificationRecevier::class.java)
            val pendingUpdateIntent = PendingIntent.getBroadcast(this, 0, updateBroadCastIntent, 0)
            am.cancel(pendingUpdateIntent)
            it.forEach { party ->
                val notifcationIntent = Intent(this, ScheduledNotificationRecevier::class.java).apply {
                    putExtra("title", party.topic)
                    putExtra("id", party.id)
                }
                val pendingIntent = PendingIntent.getBroadcast(
                    this,
                    1929, notifcationIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT
                )

                val futureInMillis =  DateTime.parse(party.time).toDate().time - Date().time
                if ( futureInMillis > 0 ) {
                    am.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, futureInMillis, pendingIntent)
                }
            }
        })
        viewModel.isLoadingUser.observe(this, Observer {
            if (it) {
                loadingDialog.show {
                    cancelable(false)  // calls setCancelable on the underlying dialog
                    cancelOnTouchOutside(false)  // calls setCanceledOnTouchOutside on the underlying dialog
                }
            } else {
                loadingDialog.dismiss()
            }
        })
        swipe_refresh_layout.isEnabled = false
        viewModel.isRefreshing.observe(this, Observer {
            swipe_refresh_layout.isRefreshing = it
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == LOGIN_SUCCESS) {
            val uid = data?.getStringExtra("uid")
            firebaseService.sendInstanceId()
            viewModel.getCurrentUser(uid!!)
            viewModel.getRooms(uid!!)
        }
    }

    override fun onSaveInstanceState(outState: Bundle?, outPersistentState: PersistableBundle?) {
        super.onSaveInstanceState(outState, outPersistentState)
        outState!!.putParcelable("user", viewModel.user.value)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.main_activity_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item!!.itemId) {
            R.id.show_menu -> {
                val bottomSheetFragment = BottomSheetFragment()
                bottomSheetFragment.show(supportFragmentManager,bottomSheetFragment.tag)
                bottomSheetFragment.arguments = Bundle().apply {
                    putParcelable("user", viewModel.user.value)
                }
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
