package com.chainslarp.app.application.network.auth

import com.chainslarp.app.application.BaseStore
import com.chainslarp.app.application.core.bindStore
import com.chainslarp.app.application.network.airtable.models.Player
import mini.Reducer
import mini.Resource
import org.kodein.di.Kodein
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.singleton

data class AuthState(
    val userLogged: Resource<Player> = Resource.empty(),
    val sendPasswordRecoveryTask: Resource<String> = Resource.empty()
)

/**
 * Class in charge of authenticating the user. Includes register and login operations.
 */
@Suppress("UndocumentedPublicFunction")
class AuthStore(kodein: Kodein) : BaseStore<AuthState>(kodein) {
    private val authController: AuthController by instance<AuthController>()

    @Reducer
    suspend fun loginUser(action: LoginUserAction) {
        if (state.userLogged.isLoading) return
        newState = state.copy(userLogged = Resource.loading())

        authController.loginWithCredentials(
            username = action.username,
            password = action.password
        ).onSuccess {
            dispatcher.dispatch(UserLoggedAction(Resource.success(it)))
        }.onFailure {
            dispatcher.dispatch(UserLoggedAction(Resource.failure(it)))
        }
    }

    @Reducer
    suspend fun userLogged(action: UserLoggedAction) {
        if (!state.userLogged.isLoading) return
        newState = state.copy(userLogged = action.player)
    }

    @Reducer
    fun onLogout(action: LogoutUserAction) {
        authController.logout()
        newState = AuthState()
    }
}

@Suppress("UndocumentedPublicClass", "UndocumentedPublicFunction")
object AuthModule {
    fun create() = Kodein.Module("AuthModule") {
        bindStore { AuthStore(kodein) }
        bind<AuthController>() with singleton {
            AuthControllerImpl(instance())
        }
    }
}