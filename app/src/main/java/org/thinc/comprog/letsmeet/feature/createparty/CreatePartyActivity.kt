package org.thinc.comprog.letsmeet.feature.createparty

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.afollestad.materialdialogs.MaterialDialog
import com.google.android.gms.location.places.Place
import com.google.android.gms.location.places.ui.PlacePicker
import com.google.android.gms.location.places.ui.PlacePicker.getPlace
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.android.synthetic.main.activity_create_party.*
import org.thinc.comprog.letsmeet.R
import org.thinc.comprog.letsmeet.data.PartyRepository
import org.thinc.comprog.letsmeet.data.model.User
import org.thinc.comprog.letsmeet.databinding.ActivityCreatePartyBinding
import javax.inject.Inject

class CreatePartyActivity : DaggerAppCompatActivity(), OnMapReadyCallback {

    @Inject
    lateinit var partyRepository: PartyRepository

    lateinit var viewModel: CreatePartyViewModel

    private val PLACE_PICKER_REQUEST = 123

    private var mMap: GoogleMap? = null

    private var selectedPlace: Place? = null

    lateinit var user: User

    lateinit var creatingPartyDialog: MaterialDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        user = intent.getParcelableExtra("user")

        viewModel = ViewModelProviders.of(this).get(CreatePartyViewModel::class.java)
        viewModel.partyRepository = partyRepository

        creatingPartyDialog = MaterialDialog(this).message(text = "Creating party...")

        viewModel.isCreatingParty.observe(this, Observer {
            if (it) {
                creatingPartyDialog.show {
                    cancelable(false)  // calls setCancelable on the underlying dialog
                    cancelOnTouchOutside(false)  // calls setCanceledOnTouchOutside on the underlying dialog
                }
            } else {
                creatingPartyDialog.dismiss()
            }
        })

        val binding: ActivityCreatePartyBinding = DataBindingUtil.setContentView(this, R.layout.activity_create_party)
        binding.executePendingBindings()
        binding.viewModel = viewModel
        binding.setLifecycleOwner(this)
        setSupportActionBar(binding.toolbar3)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Create Party"
        create_party_button.visibility = View.VISIBLE

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.create_party_map) as SupportMapFragment?
        mapFragment!!.getMapAsync(this)

        create_party_location.setOnClickListener {
            val builder = PlacePicker.IntentBuilder()
            startActivityForResult(builder.build(this), PLACE_PICKER_REQUEST)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                selectedPlace = getPlace(this, data)
                viewModel.setLocation(selectedPlace!!)
                viewModel.setLocationInMap(mMap!!, selectedPlace!!.latLng)
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap?) {
        mMap = googleMap
        mMap!!.uiSettings.setAllGesturesEnabled(false)
        if (viewModel.location.value != null) {
            viewModel.setLocation(viewModel.location.value!!)
            viewModel.setLocationInMap(mMap!!, viewModel.location.value!!.toLatLng())
        }
    }
}