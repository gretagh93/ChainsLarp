package com.chains.larp.domain.auth

import com.chains.larp.app.store.RootLogAction
import com.chains.larp.domain.auth.models.Player
import com.minikorp.duo.Action
import com.minikorp.duo.Resource
import com.minikorp.duo.TypedAction

interface LoginAction {
    @TypedAction
    data class Request(val username: String,
                        val password: String) : Action, RootLogAction

    @TypedAction
    data class Response(val resource: Resource<Player>) : Action
}

@TypedAction
object LogoutAction : Action