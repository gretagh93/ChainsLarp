package com.chainslarp.app.application.network.firebase

import com.google.firebase.firestore.FirebaseFirestore

/**
 * Get the whole device tokens collection.
 */
fun FirebaseFirestore.getDeviceTokens() = this.collection(FirestoreConstants.DeviceToken.COLLECTION)

/**
 * Get a specific document from the private user data collection based on its UID.
 */
fun FirebaseFirestore.getDeviceToken(uid: String) = this.getDeviceTokens().document(uid)

/**
 * Get the whole private user data collection.
 */
fun FirebaseFirestore.getPrivateUsersData() =
    this.collection(FirestoreConstants.User.PRIVATE_DATA_COLLECTION)

/**
 * Get a specific document from the private user data collection based on its UID.
 */
fun FirebaseFirestore.getPrivateUserDataDocument(uid: String) = getPrivateUsersData().document(uid)

/**
 * Get the whole public user data collection.
 */
fun FirebaseFirestore.getPublicUsersData() =
    this.collection(FirestoreConstants.User.PUBLIC_DATA_COLLECTION)

/**
 * Get a specific document from the public user data collection based on its UID.
 */
fun FirebaseFirestore.getPublicUserDataDocument(uid: String) = getPublicUsersData().document(uid)