package com.chainslarp.app.domain.model

import com.google.firebase.Timestamp
import com.chainslarp.app.domain.raw.FirebasePrivateUserData
import java.util.*

data class PrivateUserData(val username: String,
                           val email: String,
                           val photoUrl: String?,
                           val photoUrlThumb: String?,
                           val privateProfile: Boolean,
                           val lastLogin: Date,
                           val accountCreationDate: Date
)

fun PrivateUserData.toFirebase() = FirebasePrivateUserData(
    username = username,
    email = email,
    photoUrl = photoUrl,
    photoUrlThumb = photoUrlThumb,
    privateProfile = privateProfile,
    lastLogin = Timestamp(lastLogin),
    accountCreationDate = Timestamp(accountCreationDate)
)