package com.chainslarp.app.application.network.airtable

import com.chainslarp.app.application.network.airtable.models.Character
import mini.Action
import mini.Resource
import mini.SagaAction

@Action
data class LoadPlayerDataAction(val playerId: String) : SagaAction

@Action
data class PlayerDataLoadedAction(val playerId: String, val player: Resource<Character>)