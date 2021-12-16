package com.chainslarp.app.application.network.auth

import com.chainslarp.app.application.network.airtable.AirtableRepository
import com.minikorp.grove.Grove
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.withContext

/**
 * Implementation of [AuthController] backed by Firebase and subscriptions API.
 */
class AuthControllerImpl(private val repository: AirtableRepository) : AuthController {

    private val scope = CoroutineScope(Job())

    override suspend fun loginWithCredentials(username: String, password: String) =
        withContext(Dispatchers.IO) {
            runCatching {
                Grove.e { "Retrieve Player $username" }
                val x = repository.loginUser(username, password)
                Grove.e { " Player is $x" }
                x
            }
        }

    override fun logout() {

    }

}
