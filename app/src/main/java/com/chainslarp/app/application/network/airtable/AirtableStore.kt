package com.chainslarp.app.application.network.airtable

import com.chainslarp.app.application.BaseStore
import com.chainslarp.app.application.core.bindStore
import com.chainslarp.app.application.network.airtable.models.Character
import com.chainslarp.app.application.network.auth.LogoutUserAction
import mini.Reducer
import mini.Resource
import org.kodein.di.Kodein
import org.kodein.di.generic.instance

data class AirtableState(
    val retrievePlayers: Map<String, Resource<Character>> = emptyMap()
)

/**
 * Class in charge of authenticating the user. Includes register and login operations.
 */
@Suppress("UndocumentedPublicFunction")
class AirtableStore(kodein: Kodein) : BaseStore<AirtableState>(kodein) {
    private val airtableRepository: AirtableRepository by instance<AirtableRepository>()


    @Reducer
    suspend fun loadData(action: LoadPlayerDataAction) {
        if (state.retrievePlayers[action.playerId]?.isLoading == true) return
        newState =
            state.copy(retrievePlayers = state.retrievePlayers.plus(action.playerId to Resource.loading()))

        airtableRepository.fetchUser(id = action.playerId)
            .onSuccess {
                dispatcher.dispatch(PlayerDataLoadedAction(action.playerId, Resource.success(it!!)))
            }.onFailure {
                dispatcher.dispatch(PlayerDataLoadedAction(action.playerId, Resource.failure(it)))
            }
    }

    @Reducer
    fun dataLoaded(action: PlayerDataLoadedAction) {
        if (state.retrievePlayers[action.playerId]?.isLoading == false) return
        newState =
            state.copy(retrievePlayers = state.retrievePlayers.plus(action.playerId to action.player))
    }

    @Reducer
    fun onLogout(action: LogoutUserAction) {
        newState = initialState()
    }
}

@Suppress("UndocumentedPublicClass", "UndocumentedPublicFunction")
object AirtableModule {
    fun create() = Kodein.Module("AirtableModule") {
        bindStore { AirtableStore(kodein) }
    }
}