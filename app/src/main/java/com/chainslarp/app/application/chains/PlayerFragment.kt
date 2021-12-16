package com.chainslarp.app.application.chains

import android.os.Bundle
import com.chainslarp.app.R
import com.chainslarp.app.application.BaseFragment
import com.chainslarp.app.application.core.toast
import com.chainslarp.app.application.network.airtable.AirtableStore
import com.chainslarp.app.application.network.airtable.LoadPlayerDataAction
import com.chainslarp.app.application.network.airtable.models.CharacterFields
import com.chainslarp.app.utils.argument
import kotlinx.coroutines.flow.onEach
import mini.flow.flow
import mini.flow.select
import org.kodein.di.generic.instance

class PlayerFragment : BaseFragment() {

    companion object {
        const val TAG = "player_fragment"
    }

    override val layout = R.layout.scanning_fragment
    private val userId by argument<String>("userId")
    private val playerStore by instance<AirtableStore>()

    override suspend fun whenCreated(savedInstanceState: Bundle?) {
        super.whenCreated(savedInstanceState)

        dispatcher.dispatchOnUi(LoadPlayerDataAction(playerId = userId))

        playerStore.flow()
            .select { it.retrievePlayers[userId] }
            .onEach { resource ->
                when {
                    resource!!.isLoading -> showLoadingUi(true)
                    resource.isSuccess -> onDataLoaded(resource.getOrNull()!!.fields)
                    resource.isFailure -> {
                        showLoadingUi(false)
                        toast("Se produjo un error al cargar los datos del usuario")
                    }
                }
            }.launchOnUi()
    }

    private fun showLoadingUi(show: Boolean) {

    }

    private fun onDataLoaded(fields: CharacterFields) {

    }
}