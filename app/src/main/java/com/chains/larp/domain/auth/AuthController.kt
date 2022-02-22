package com.chains.larp.domain.auth

import com.chains.larp.domain.AirtableRepository
import com.chains.larp.domain.auth.models.Player
import com.minikorp.grove.Grove
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.withContext

/**
 * Interface that user authorization controllers must comply to.
 */
interface AuthController {

    /**
     * Logs the given user in the system.
     */
    suspend fun loginWithCredentials(username: String, password: String): Result<Player>

    /**
     * Logs the current user out.
     */
    fun logout()
}

/**
 * Implementation of [AuthController] backed by Firebase and subscriptions API.
 */
class AuthControllerImpl(private val repository: AirtableRepository) : AuthController {

    private val scope = CoroutineScope(Job())

    override suspend fun loginWithCredentials(username: String, password: String) =
        withContext(Dispatchers.IO) {
            runCatching {
                Grove.e { "Retrieve Player for user $username" }
                val player = repository.loginUser(username, password)
                Grove.e { " Player is $player" }
                player
            }
        }

    override fun logout() {

    }

}
