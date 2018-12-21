package org.thinc.comprog.letsmeet.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import org.joda.time.DateTime
import java.util.*

@Parcelize
data class Party(
    val id: String = "",
    val members: List<String> = emptyList(),
    val topic: String = "",
    val code: String = "",
    val chat: Chat = Chat(),
    val messages: List<ChatMessage> = emptyList(),
    val location: Location = Location(),
    val time: String = DateTime(Calendar.getInstance().time).toString(),
    val public: Boolean = false
) : Parcelable