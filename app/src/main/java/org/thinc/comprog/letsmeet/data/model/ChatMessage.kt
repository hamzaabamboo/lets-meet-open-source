package org.thinc.comprog.letsmeet.data.model

import android.os.Parcelable
import com.stfalcon.chatkit.commons.models.IMessage
import com.stfalcon.chatkit.commons.models.IUser
import kotlinx.android.parcel.Parcelize
import org.joda.time.DateTime
import java.util.*

@Parcelize
data class ChatMessage(
    val content: String = "",
    private val id: String = "",
    val time: String = "",
    val sender: String = "",
    var senderUser: User = User("Unknown", "Unknown User", "")
) : IMessage, Parcelable {

    val dateTime: Date = if (time.isNotEmpty()) {
        DateTime.parse(time).toDate()
    } else {
        Calendar.getInstance().time
    }

    override fun getId(): String {
        return id
    }

    override fun getCreatedAt(): Date {
        return dateTime
    }

    override fun getText(): String {
        return content
    }

    override fun getUser(): IUser {
        return senderUser
    }
}