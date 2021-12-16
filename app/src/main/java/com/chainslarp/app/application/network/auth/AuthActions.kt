package com.chainslarp.app.application.network.auth

import com.google.firebase.auth.FirebaseUser
import com.chainslarp.app.application.crashreporting.AddCrashReportingUserDataAction
import com.chainslarp.app.application.network.airtable.models.Player
import mini.Action
import mini.Resource
import mini.SagaAction


/**
 * Action to login a user.
 */
@Action
data class LoginUserAction(
    val username: String,
    val password: String
) : SagaAction

@Action
data class UserLoggedAction(val player : Resource<Player>)

/**
 * Action to login anonymously.
 */
@Action
object LoginAnonymouslyAction

/**
 * Action to logout a user.
 */
@Action
object LogoutUserAction