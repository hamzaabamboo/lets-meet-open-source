package org.thinc.comprog.letsmeet.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class UserStat(
    var partiesJoined: Int = 0,
    var partiesDeclined: Int = 0,
    var partiesCreated: Int = 0
) : Parcelable