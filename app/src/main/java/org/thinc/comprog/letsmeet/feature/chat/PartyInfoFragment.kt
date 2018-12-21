package org.thinc.comprog.letsmeet.feature.chat

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import org.thinc.comprog.letsmeet.R
import android.view.LayoutInflater
import kotlinx.android.synthetic.main.dialog_party_info.view.*
import org.joda.time.format.ISODateTimeFormat
import org.thinc.comprog.letsmeet.data.model.Party
import java.text.SimpleDateFormat
import java.util.*


class PartyInfoFragment : DialogFragment() {

    @SuppressLint("InflateParams")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            // Use the Builder class for convenient dialog construction
            val party = arguments!!.getParcelable<Party>("party")
            val builder = AlertDialog.Builder(it)
            val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_party_info, null)
            with(dialogView) {
                val time = ISODateTimeFormat.dateTime().parseDateTime(party?.time).toDate()
                val dateFormat = "EEE dd MMM yyyy"
                val timeFormat = "hh:mm aa"
                party_topic.text = party?.topic
                party_code.text = party?.code
                party_date.text  = SimpleDateFormat(dateFormat, Locale.US).format(time)
                party_time.text  = SimpleDateFormat(timeFormat, Locale.US).format(time)
                party_location.text = party?.location?.name
            }
            builder.setView(dialogView)
            // Create the AlertDialog object and return it
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    fun newInstance(party: Party) = PartyInfoFragment().apply {
        arguments = Bundle().also {
            it.putParcelable("party", party)
        }
    }
}