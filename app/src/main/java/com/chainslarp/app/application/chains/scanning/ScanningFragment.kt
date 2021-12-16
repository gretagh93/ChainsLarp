package com.chainslarp.app.application.chains.scanning

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.nfc.NfcManager
import android.os.Bundle
import android.util.Log
import com.chainslarp.app.R
import com.chainslarp.app.application.BaseFragment
import com.chainslarp.app.application.chains.PlayerFragment
import com.chainslarp.app.application.core.app
import com.chainslarp.app.application.network.airtable.AirtableRepository
import com.chainslarp.app.application.network.auth.AuthStore
import kotlinx.android.synthetic.main.scanning_fragment.*
import kotlinx.coroutines.flow.onEach
import mini.flow.flow
import mini.flow.select
import org.kodein.di.generic.instance


class ScanningFragment : BaseFragment() {

    companion object {
        const val TAG = "scanning_fragment"
    }

    override val layout = R.layout.scanning_fragment
    private val authStore by instance<AuthStore>()
    private val repository by instance<AirtableRepository>()

    override suspend fun whenCreated(savedInstanceState: Bundle?) {
        super.whenCreated(savedInstanceState)

        authStore.flow()
            .select { it.userLogged.getOrNull() }
            .onEach { player ->
                welcome_title.text = "Bienvenid@ ${player!!.fields.username}"
            }.launchOnUi()

        pulsator.start()
    }

    private fun navigateToPlayerFragment(playerId: String) {
        parentFragmentManager.beginTransaction()
            .add(R.id.mainContainer,
                PlayerFragment().apply {
                    arguments = Bundle().apply { putString("userId", playerId) }
                },
                PlayerFragment.TAG
            )
            .commit()
    }
}