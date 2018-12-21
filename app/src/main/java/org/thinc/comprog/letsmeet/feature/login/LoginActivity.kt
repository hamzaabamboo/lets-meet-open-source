package org.thinc.comprog.letsmeet.feature.login

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.Signature
import android.os.Bundle
import android.util.Base64
import android.util.Log
import androidx.databinding.DataBindingUtil
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth
import dagger.android.support.DaggerAppCompatActivity
import org.thinc.comprog.letsmeet.R
import org.thinc.comprog.letsmeet.databinding.ActivityLoginBinding
import org.thinc.comprog.letsmeet.feature.main.MainActivity
import org.thinc.comprog.letsmeet.feature.main.MainActivity.Companion.RC_SIGN_IN
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import javax.inject.Inject

const val LOGIN_SUCCESS = 1234
const val LOGIN_FAILED = 1235

class LoginActivity : DaggerAppCompatActivity() {
    lateinit var binding: ActivityLoginBinding

    @Inject
    lateinit var viewModel: LoginActivityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val auth = FirebaseAuth.getInstance()
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        binding.executePendingBindings()

        binding.viewModel = viewModel
        binding.setLifecycleOwner(this)
        if (auth.currentUser != null) {
            val user = auth.currentUser?.displayName
            val uid = auth.currentUser?.uid
            val intent = Intent(this, MainActivity::class.java).apply {
                putExtra("username", user)
                putExtra("uid", uid)
            }
            startActivity(intent)
        } else {
            viewModel.setLoggedInStatus(false)
        }
    }

    override fun onBackPressed() {
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val response = IdpResponse.fromResultIntent(data)

            if (resultCode == Activity.RESULT_OK) {
                val user = FirebaseAuth.getInstance().currentUser?.displayName
                val uid = FirebaseAuth.getInstance().currentUser?.uid
                val intent = Intent().apply {
                    putExtra("username", user)
                    putExtra("uid", uid)
                }
                setResult(LOGIN_SUCCESS, intent)
                finish()
            }
        }
    }
}