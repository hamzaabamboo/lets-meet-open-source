package org.thinc.comprog.letsmeet.feature.profile

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.thinc.comprog.letsmeet.data.model.User

class ProfileActivityViewModel: ViewModel() {
    val user = MutableLiveData<User>()
}