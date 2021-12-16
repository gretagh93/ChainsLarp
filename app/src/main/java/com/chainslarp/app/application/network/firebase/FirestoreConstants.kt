package com.chainslarp.app.application.network.firebase

/**
 * Constants used to identify the Firestore data model.
 */
object FirestoreConstants {

    /**
     * User collections along with their document fields.
     */
    object User {
        const val PRIVATE_DATA_COLLECTION = "privateUserData"
        const val PUBLIC_DATA_COLLECTION = "publicUserData"
    }

    /**
     * DeviceToken collection along with its document fields.
     */
    object DeviceToken {
        const val COLLECTION = "deviceTokens"
    }
}