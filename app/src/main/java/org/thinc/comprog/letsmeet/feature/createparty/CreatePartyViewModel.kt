package org.thinc.comprog.letsmeet.feature.createparty

import android.app.Activity
import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.app.TimePickerDialog
import android.content.Intent
import android.view.View
import androidx.databinding.InverseMethod
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.location.places.Place
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_create_party.view.*
import org.joda.time.DateTime
import org.thinc.comprog.letsmeet.common.DisposableTracker
import org.thinc.comprog.letsmeet.data.PartyRepository
import org.thinc.comprog.letsmeet.data.model.CreateParty
import org.thinc.comprog.letsmeet.data.model.EditParty
import org.thinc.comprog.letsmeet.data.model.Location
import org.thinc.comprog.letsmeet.feature.addparty.AddPartyActivity
import java.text.SimpleDateFormat
import java.util.*


class CreatePartyViewModel : ViewModel(), DisposableTracker {
    lateinit var partyRepository: PartyRepository
    lateinit var partyId: String
    private val calendar = Calendar.getInstance()
    private val date = MutableLiveData<String>()
    private val time = MutableLiveData<String>()
    private val locationText = MutableLiveData<String>()
    val location = MutableLiveData<Location>()
    val topic = MutableLiveData<String>().apply { postValue("") }
    val public = MutableLiveData<Boolean>().apply { postValue(false) }
    val isCreatingParty = MutableLiveData<Boolean>().apply { value = false }
    override val compositeDisposable = CompositeDisposable()

    fun setTopic(topic: String) = this.topic.postValue(topic)
    fun setPublic(public: Boolean) = this.public.postValue(public)
    private val dateSetListener =
        OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, monthOfYear)
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
//            val dateFormat = "EEE dd MMM yyyy "
//            val sdf = SimpleDateFormat(dateFormat, Locale.US)
//            date.postValue(sdf.format(calendar.time))
            setDate()
        }

    private val timePickerListener =
        TimePickerDialog.OnTimeSetListener { _, hourOfDay, minutes ->
            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
            calendar.set(Calendar.MINUTE, minutes)
//            val timeFormat = "hh:mm aa "
//            val sdf = SimpleDateFormat(timeFormat, Locale.US)
//            time.postValue(sdf.format(calendar.time))
            setTime()
        }

    fun getDate(): LiveData<String> = date
    private fun setDate() {
        val dateFormat = "EEE dd MMM yyyy "
        val sdf = SimpleDateFormat(dateFormat, Locale.US)
        date.postValue(sdf.format(calendar.time))
    }
    fun getTime(): LiveData<String> = time
    private fun setTime() {
        val timeFormat = "hh:mm aa "
        val sdf = SimpleDateFormat(timeFormat, Locale.US)
        time.postValue(sdf.format(calendar.time))
    }

    private fun getMeetingTime() = DateTime(calendar.time).toString()
    fun setMeetingTime(timeString: String) {
        val time = DateTime.parse(timeString).toDate()
        calendar.time = time
        setDate()
        setTime()
    }
    fun setLocation(place: Place) {
        place.run {
            Location(
                latLng.latitude, latLng.longitude, name.toString()
            )
        }.let {
            location.postValue(it)
            locationText.postValue("${it.name}")
        }
    }
    fun setLocation(location: Location){
        this.location.postValue(location)
        this.locationText.postValue("${location.name}")
    }

    fun setLocationInMap(mMap: GoogleMap, latLng: LatLng) {
        mMap.clear()
        mMap.addMarker(
            MarkerOptions().position(latLng).title("Party Location").icon(
                BitmapDescriptorFactory.defaultMarker(50.0f)
            )
        )
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15.5f))
    }

    fun getLocationText(): LiveData<String> = locationText

    fun timePickerDialog(v: View) = TimePickerDialog(
        v.context, timePickerListener,
        calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false
    ).show()

    fun datePickerDialog(v: View) = DatePickerDialog(
        v.context, dateSetListener,
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    ).show()

    fun createParty(v: View) {
        val party = CreateParty(
            location = location.value ?: Location(),
            topic = topic.value!!,
            time = getMeetingTime(),
            public = public.value!!
        )
        v.create_party_button.isClickable = false
        isCreatingParty.postValue(true)

        partyRepository.createParty(party)!!.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { res ->
                v.create_party_button.isClickable = true
                val intent = Intent().apply {
                    putExtra("party", res)
                }
                (v.context as Activity).setResult(AddPartyActivity.CREATE_PARTY_SUCCESS, intent)
                (v.context as Activity).finish()
            }.track()
    }

    fun editParty(v: View) {
        val party = EditParty(
            party = partyId,
            location = location.value ?: Location(),
            topic = topic.value!!,
            time = getMeetingTime(),
            public = public.value!!
        )
        isCreatingParty.postValue(true)

        partyRepository.editParty(party)!!.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { res ->
                val intent = Intent().apply {
                    putExtra("party", res)
                }
                (v.context as Activity).setResult(AddPartyActivity.CREATE_PARTY_SUCCESS, intent)
                (v.context as Activity).finish()
            }.track()
    }

    @InverseMethod("myBox")
    fun myUnbox(b: Boolean?): Boolean {
        return when (b) {
            true -> true
            false -> false
            null -> false
        }
    }

    fun myBox(b: Boolean): Boolean? {
        return if (b) java.lang.Boolean.TRUE else java.lang.Boolean.FALSE
    }

    override fun onCleared() {
        super.onCleared()
        cleanUpDisposable()
    }
}
