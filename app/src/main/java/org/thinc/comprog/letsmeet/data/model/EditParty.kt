package org.thinc.comprog.letsmeet.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import org.joda.time.DateTime
import java.util.*

@Parcelize
data class EditParty(
    val party: String = "",
    val location: Location = Location(),
    val topic: String = "",
    val time: String =
        DateTime(Calendar.getInstance().time).toString(),
    val public: Boolean = false
): Parcelable {
    fun toMap(): Map<String, Any> = hashMapOf(
        "party" to party,
        "location" to location.toMap(),
        "topic" to topic,
        "time" to time,
        "public" to public
    )
}