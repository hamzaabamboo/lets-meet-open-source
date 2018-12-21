package org.thinc.comprog.letsmeet.feature.addparty

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.afollestad.materialdialogs.MaterialDialog
import com.google.firebase.auth.FirebaseAuth
import dagger.android.support.DaggerAppCompatActivity
import io.reactivex.disposables.CompositeDisposable
import org.thinc.comprog.letsmeet.R
import org.thinc.comprog.letsmeet.data.PartyRepository
import org.thinc.comprog.letsmeet.data.UserRepository
import org.thinc.comprog.letsmeet.data.model.Party
import org.thinc.comprog.letsmeet.data.model.User
import org.thinc.comprog.letsmeet.databinding.ActivityAddPartyBinding
import org.thinc.comprog.letsmeet.feature.chat.ChatActivity
import org.thinc.comprog.letsmeet.feature.login.LoginActivity
import org.thinc.comprog.letsmeet.feature.main.MainActivity.Companion.LOGIN
import javax.inject.Inject

class AddPartyActivity : DaggerAppCompatActivity() {

    companion object {
        const val OPEN_QR_CODE = "org.thinc.comprog.letsmeet.OPEN_QR_CODE"
        const val ADD_PARTY_QR = 21412
        const val ADD_PARTY_QR_SUCCESS = 21413
        const val ADD_PARTY_QR_FAILED = 21414
        const val CREATE_PARTY = 21415
        const val CREATE_PARTY_SUCCESS = 21416
        const val JOIN_PUBLIC_PARTY = 21417
        const val JOIN_PUBLIC_PARTY_SUCCESS = 21418
    }

    @Inject
    lateinit var viewModel: AddPartyActivityViewModel
    @Inject
    lateinit var partyRepository: PartyRepository
    @Inject
    lateinit var userRepository: UserRepository

    lateinit var binding: ActivityAddPartyBinding

    private val compositeDisposable = CompositeDisposable()
    private lateinit var joiningDialog: MaterialDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_party)

        joiningDialog = MaterialDialog(this).message(text = "Joining Party")

        viewModel.isJoiningParty.observe(this, Observer {
            if (it) {
                joiningDialog.show {
                    cancelable(false)  // calls setCancelable on the underlying dialog
                    cancelOnTouchOutside(false)  // calls setCanceledOnTouchOutside on the underlying dialog
                }
            } else {
                joiningDialog.dismiss()
            }
        })
        val auth = FirebaseAuth.getInstance()
        if ( auth.currentUser == null ) {

        }
        if (intent.hasExtra("user")) {

        } else {
            val uid: String?
            val action = intent.action
            val appLinkData: Uri? = intent.data
            if (Intent.ACTION_VIEW == action) {
                val partyId = appLinkData?.lastPathSegment!!
                if (auth.currentUser != null) {
                    uid = auth.currentUser?.uid!!
                    compositeDisposable.add(
                        userRepository.getCurrentUser(uid).subscribe {
                            val user = it.toObject(User::class.java)!!.copy(id=it.id)
                            partyRepository.joinParty(partyId).subscribe {
                                val intent = Intent(this, ChatActivity::class.java).apply {
                                    putExtra("party", it)
                                    putExtra("user", user)
                                }
                                finish()
                                startActivity(intent)
                            }
                        })
                } else {
                    val intent = Intent(this, LoginActivity::class.java)
                    intent.putExtra("joiningParty", partyId)
                    startActivityForResult(intent, LOGIN)
                }
            }
            else if ( action == OPEN_QR_CODE) {
                if (auth.currentUser != null) {
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                        == PackageManager.PERMISSION_DENIED
                    ) {
                        ActivityCompat.requestPermissions(
                            this, arrayOf(Manifest.permission.CAMERA),
                            AddPartyActivity.ADD_PARTY_QR_SUCCESS
                        )

                    } else {
                        val intent = Intent(this, QRCodeActivity::class.java)
                        this.startActivityForResult(
                            intent, ADD_PARTY_QR
                        )
                    }
                } else {
                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                }
            }
        }
        val user = intent.getParcelableExtra<User>("user")
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_party)
        binding.executePendingBindings()

        binding.viewModel = viewModel
        setSupportActionBar(binding.toolbar2)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Add party"
        binding.setLifecycleOwner(this)

        viewModel.partyRepository = partyRepository
        viewModel.user.value = user
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            ADD_PARTY_QR_SUCCESS -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    val intent = Intent(this, QRCodeActivity::class.java)
                    startActivityForResult(
                        intent, ADD_PARTY_QR
                    )
                } else {
                    Toast.makeText(
                        this, "Please allow camera access !!",
                        Toast.LENGTH_LONG
                    ).show()
                }
                return
            }
            else -> {

            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.dispose()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == ADD_PARTY_QR_SUCCESS) {
            val path = data?.getStringExtra("result")
            val partyId = path!!.substring(path.lastIndexOf('/') + 1)
            viewModel.isJoiningParty.value = true
            compositeDisposable.add(partyRepository.joinParty(partyId).subscribe {
                val intent = Intent(this, ChatActivity::class.java).apply {
                    putExtra("party", it)
                    putExtra("user", viewModel.user.value)
                }
                viewModel.isJoiningParty.postValue(false)
                finish()
                startActivity(intent)
            })

        }
        if (resultCode == CREATE_PARTY_SUCCESS) {
            val party = data!!.getParcelableExtra<Party>("party")
            viewModel.isJoiningParty.value = true
            val intent = Intent(this, ChatActivity::class.java).apply {
                this.putExtra("party", party)
                this.putExtra("user", viewModel.user.value)
            }
            viewModel.isJoiningParty.value = false
            finish()
            startActivity(intent)
        }
        if (resultCode == JOIN_PUBLIC_PARTY_SUCCESS) {
            val party = data!!.getParcelableExtra<Party>("party")
            viewModel.isJoiningParty.value = true
            compositeDisposable.add(partyRepository.joinParty(party.code).subscribe {
                val intent = Intent(this, ChatActivity::class.java).apply {
                    putExtra("party", it)
                    putExtra("user", viewModel.user.value)
                }
                viewModel.isJoiningParty.postValue(false)
                finish()
                startActivity(intent)
            })
        }
    }
}