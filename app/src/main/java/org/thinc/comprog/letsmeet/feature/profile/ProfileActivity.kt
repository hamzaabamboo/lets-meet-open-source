package org.thinc.comprog.letsmeet.feature.profile

import android.os.Bundle
import android.view.MenuItem
import androidx.databinding.DataBindingUtil
import dagger.android.support.DaggerAppCompatActivity
import org.thinc.comprog.letsmeet.R
import org.thinc.comprog.letsmeet.databinding.ActivityProfileBinding
import javax.inject.Inject

class ProfileActivity : DaggerAppCompatActivity() {

    lateinit var binding: ActivityProfileBinding

    @Inject
    lateinit var viewModel: ProfileActivityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_profile
        )
        binding.viewModel = viewModel
        viewModel.user.value = intent.getParcelableExtra("user")
        setSupportActionBar(binding.toolbar4)
        supportActionBar?.title = "User Profile"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
