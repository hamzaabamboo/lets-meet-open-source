package org.thinc.comprog.letsmeet.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Poll(
    val title: String,
    val type: PollType,
    val choice: List<PollChoice>
) : Parcelable
