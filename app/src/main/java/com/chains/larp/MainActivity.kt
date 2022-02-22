package com.chains.larp

import android.content.Context
import android.content.Intent
import android.nfc.NfcAdapter
import android.nfc.NfcManager
import android.nfc.Tag
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.chains.larp.app.ChainsApp
import com.chains.larp.app.appContext
import com.chains.larp.app.store.AppStore
import com.chains.larp.domain.nfc.ManageNfcReaderState
import com.chains.larp.domain.nfc.ReadCharacterMedalAction
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.kodein.di.DIAware
import org.kodein.di.android.closestDI
import org.kodein.di.instance

class MainActivity : ComponentActivity(), DIAware {
    override val di by closestDI(appContext)

    private val scope = CoroutineScope(Dispatchers.Main)

    val store: AppStore by instance()
    private val nfcManager by lazy { getSystemService(Context.NFC_SERVICE) as NfcManager }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ChainsApp()
        }
    }

    override fun onResume() {
        super.onResume()
        enableNfcForegroundDispatch()
    }

    override fun onPause() {
        disableNfcForegroundDispatch()
        super.onPause()
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        if (intent.action == NfcAdapter.ACTION_TAG_DISCOVERED) {
            val tagFromIntent = intent.getParcelableExtra<Tag>(NfcAdapter.EXTRA_TAG)
            scope.launch {
                store.dispatch(ReadCharacterMedalAction.Request(tagFromIntent))
            }
        }
    }

    private fun disableNfcForegroundDispatch() {
        scope.launch {
                store.dispatch(ManageNfcReaderState(false, this@MainActivity))
            }
    }

    private fun enableNfcForegroundDispatch() {
        scope.launch {
            store.dispatch(ManageNfcReaderState(true, this@MainActivity))
        }
    }
}