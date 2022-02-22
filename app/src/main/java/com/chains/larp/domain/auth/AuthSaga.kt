package com.chains.larp.domain.auth

import com.chains.larp.app.BootstrapAction
import com.chains.larp.app.store.BaseSaga
import com.chains.larp.domain.auth.models.Player
import com.minikorp.duo.*
import org.kodein.di.DI
import org.kodein.di.instance
import java.io.Serializable

@State
data class AuthState(val userLogged: Resource<Player> = Resource.empty()) : Serializable

@TypedHandler
class AuthSaga(di: DI) : BaseSaga<AuthState>(di) {
    private val controller: AuthController by instance()

    @TypedHandler.Fun
    suspend fun bootstrap(context: DispatchContext<AuthState>, action: BootstrapAction) {

    }

    @TypedHandler.Fun
    suspend fun handleLogin(context: DispatchContext<AuthState>, action: LoginAction.Request) {
        context.reduce { it.copy(userLogged = Resource.loading()) }
        controller.loginWithCredentials(action.username, action.password)
            .onSuccess { context.dispatch(LoginAction.Response(Resource.success(it))) }
            .onFailure { context.dispatch(LoginAction.Response(Resource.failure(it))) }
    }

    @TypedHandler.Fun
    suspend fun logout(context: DispatchContext<AuthState>, action: LogoutAction) {
        controller.logout()
        context.reduce { AuthState() }
    }

    @TypedHandler.Root
    override suspend fun handle(context: DispatchContext<AuthState>, action: Action) {
        handleTyped(context, action)
    }
}

@TypedHandler
class AuthReducer : Reducer<AuthState> {

    @TypedHandler.Fun
    fun handleLoginResponse(state: AuthState, action: LoginAction.Response): AuthState {
        return state.copy(userLogged = action.resource)
    }

    @TypedHandler.Root
    override fun reduce(state: AuthState, action: Action): AuthState {
        return reduceTyped(state, action) ?: state
    }
}