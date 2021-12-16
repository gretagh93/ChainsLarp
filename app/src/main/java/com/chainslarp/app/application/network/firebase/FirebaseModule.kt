package com.chainslarp.app.application.network.firebase

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.iid.FirebaseInstanceId
import org.kodein.di.Kodein
import org.kodein.di.generic.bind
import org.kodein.di.generic.singleton

object FirebaseModule {
    fun create() = Kodein.Module("firebase") {

        bind<FirebaseFirestore>() with singleton {
            val settings = FirebaseFirestoreSettings.Builder()
                // Enable cache for offline mode
                .setPersistenceEnabled(true)
                .build()
            FirebaseFirestore.getInstance().apply { firestoreSettings = settings }
        }

        bind<FirebaseAuth>() with singleton {
            FirebaseAuth.getInstance()
        }

        bind<FirebaseInstanceId>() with singleton {
            FirebaseInstanceId.getInstance()
        }
    }
}