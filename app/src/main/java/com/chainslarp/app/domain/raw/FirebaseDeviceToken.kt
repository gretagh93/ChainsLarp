package com.chainslarp.app.domain.raw

import com.google.firebase.iid.FirebaseInstanceId

/**
 * Firebase representation of the device token retrieved from [FirebaseInstanceId].
 */
data class FirebaseDeviceToken(val deviceToken: String = "")