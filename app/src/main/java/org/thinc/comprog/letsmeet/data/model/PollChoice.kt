package org.thinc.comprog.letsmeet.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PollChoice(
    var text: String,
    var selected: List<User>
) : Parcelable