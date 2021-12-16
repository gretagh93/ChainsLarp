package com.chainslarp.app.application.network.auth

import com.chainslarp.app.application.network.airtable.models.Player

/**
 * Interface that user authorization controllers must comply to.
 */
interface AuthController {

    /**
     * Logs the given user in the system.
     */
    suspend fun loginWithCredentials(username: String,
                                     password: String): Result<Player>

    /**
     * Logs the current user out.
     */
    fun logout()
}
