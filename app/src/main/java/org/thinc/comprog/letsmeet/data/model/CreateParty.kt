package org.thinc.comprog.letsmeet.data.model

import org.joda.time.DateTime
import java.util.*

data class CreateParty(
    val location: Location = Location(),
    val topic: String = "",
    val time: String =
        DateTime(Calendar.getInstance().time).toString(),
    val public: Boolean = false
) {
    fun toMap(): Map<String, Any> = hashMapOf(
        "location" to location.toMap(),
        "topic" to topic,
        "time" to time,
        "public" to public
    )
}