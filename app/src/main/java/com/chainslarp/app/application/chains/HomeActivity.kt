package com.chainslarp.app.application.chains

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.nfc.FormatException
import android.nfc.NfcAdapter
import android.nfc.NfcManager
import android.nfc.Tag
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.chainslarp.app.R
import com.chainslarp.app.application.chains.scanning.ScanningFragment
import com.chainslarp.app.application.core.app
import com.chainslarp.app.utils.MCReader
import com.chainslarp.app.utils.NfcTag
import com.minikorp.grove.Grove
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import mini.rx.DefaultSubscriptionTracker
import mini.rx.SubscriptionTracker
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import kotlin.coroutines.CoroutineContext


class HomeActivity : AppCompatActivity(),
    SubscriptionTracker by DefaultSubscriptionTracker(),
    KodeinAware {

    private val nfcManager by lazy { getSystemService(Context.NFC_SERVICE) as NfcManager }

    override val kodein = Kodein {
        extend(app.kodein)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.home_activity)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .add(
                    R.id.mainContainer,
                    ScanningFragment(), ScanningFragment.TAG
                )
                .commit()
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
        val tagFromIntent = intent.getParcelableExtra<Tag>(NfcAdapter.EXTRA_TAG)
        try {
            lifecycleScope.launch(Dispatchers.IO) {
                val tag = NfcTag(tagFromIntent)
                val data = tag.readData()
                Grove.e { data }
            }
        } catch (e: FormatException) {
            Log.e("Main", "Unsupported tag tapped", e)
            e.printStackTrace()
            return
        }
        //tagId = tag!!.tagId
        //showToast("Tag tapped: $tagId")

        // if (NfcAdapter.ACTION_TAG_DISCOVERED == intent.action) {
        //     val rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES)
        //     if (rawMsgs != null) {
        //         //onTagTapped(NfcUtils.getUID(intent), NfcUtils.getData(rawMsgs))
        //     }
        // }
    }

    private fun disableNfcForegroundDispatch() {
        try {
            nfcManager.defaultAdapter?.disableForegroundDispatch(this)
        } catch (ex: IllegalStateException) {
            Log.e("d", "Error disabling NFC foreground dispatch", ex)
        }
    }

    private fun enableNfcForegroundDispatch() {
        try {
            val intent = Intent(this, javaClass).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)

            val nfcPendingIntent = PendingIntent.getActivity(this, 0, intent, 0)
            nfcManager.defaultAdapter?.enableForegroundDispatch(this, nfcPendingIntent, null, null)
        } catch (ex: IllegalStateException) {
            Log.e("x", "Error enabling NFC foreground dispatch", ex)
        }
    }
}