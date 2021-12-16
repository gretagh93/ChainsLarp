package com.chainslarp.app.domain.raw

import com.google.firebase.Timestamp
import com.chainslarp.app.domain.model.PrivateUserData

data class FirebasePrivateUserData(val username: String = "",
                                   val email: String = "",
                                   val photoUrl: String? = null,
                                   val photoUrlThumb: String? = null,
                                   val privateProfile: Boolean = false,
                                   val lastLogin: Timestamp = Timestamp.now(),
                                   val accountCreationDate: Timestamp = Timestamp.now()
)

fun FirebasePrivateUserData.toModel() = PrivateUserData(
    username = username,
    email = email,
    photoUrl = photoUrl,
    photoUrlThumb = photoUrlThumb,
    privateProfile = privateProfile,
    lastLogin = lastLogin.toDate(),
    accountCreationDate = accountCreationDate.toDate()
)