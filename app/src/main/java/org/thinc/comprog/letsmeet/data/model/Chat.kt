package org.thinc.comprog.letsmeet.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Chat(
    val currentPoll: Poll? = null,
    val read: List<User>? = listOf()
) : Parcelable