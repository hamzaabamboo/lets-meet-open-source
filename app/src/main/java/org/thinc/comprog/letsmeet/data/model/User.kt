package org.thinc.comprog.letsmeet.data.model

import android.os.Parcelable
import com.stfalcon.chatkit.commons.models.IUser
import kotlinx.android.parcel.Parcelize

@Parcelize
data class User(
    private val id: String = "",
    private val name: String = "",
    private val avatar: String = "",
    val stats: UserStat = UserStat(),
    val currentParties: List<String> = listOf(),
    val location: Location? = null
) : IUser, Parcelable {
    override fun getAvatar(): String {
        return avatar
    }

    override fun getId(): String {
        return id
    }

    override fun getName(): String {
        return name
    }
}