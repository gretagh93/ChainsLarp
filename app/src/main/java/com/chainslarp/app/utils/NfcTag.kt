package com.chainslarp.app.utils

import android.nfc.FormatException
import android.nfc.NdefMessage
import android.nfc.Tag
import android.nfc.tech.MifareClassic
import android.nfc.tech.Ndef
import android.nfc.tech.NdefFormatable
import android.util.Log
import com.minikorp.grove.Grove
import java.io.IOException
import java.util.*


class NfcTag @Throws(FormatException::class) constructor(tag: Tag) {
    private val NDEF = Ndef::class.java.canonicalName
    private val NDEF_FORMATABLE = NdefFormatable::class.java.canonicalName
    private val MIFARE = MifareClassic::class.java.canonicalName

    private val ndef: Ndef?
    private val ndefFormatable: NdefFormatable?
    private val mifareClassic: MifareClassic?

    val tagId: String?
        get() {
            if (ndef != null) {
                return bytesToHexString(ndef.tag.id)
            } else if (ndefFormatable != null) {
                return bytesToHexString(ndefFormatable.tag.id)
            }
            return null
        }

    init {
        val technologies = tag.techList
        val tagTechs = Arrays.asList(*technologies)
        when {
            tagTechs.contains(NDEF) -> {
                Log.i("WritableTag", "contains ndef")
                ndef = Ndef.get(tag)
                ndefFormatable = null
                mifareClassic = null
            }
            tagTechs.contains(MIFARE) -> {
                mifareClassic = MifareClassic.get(tag)
                ndef = null
                ndefFormatable = null
            }
            tagTechs.contains(NDEF_FORMATABLE) -> {
                Log.i("WritableTag", "contains ndef_formatable")
                ndefFormatable = NdefFormatable.get(tag)
                ndef = null
                mifareClassic = null
            }
            else -> {
                throw FormatException("Tag doesn't support ndef")
            }
        }
    }

    fun readData(): String {
        if (mifareClassic == null) return "null"
        try {
            //Variables
            val sectorCount: Int = mifareClassic.sectorCount
            val tagSize: Int = mifareClassic.size
            //Keys
            var defaultKeys: ByteArray? = byteArrayOf()
            defaultKeys = MifareClassic.KEY_DEFAULT
            var values: String = ""
            //Connecting to tag
            mifareClassic.connect()
            for (sectorIndex in 0 until sectorCount) {
                val auth = mifareClassic.authenticateSectorWithKeyA(sectorIndex, defaultKeys)
                if (auth) {
                    val sectorBlockCount = mifareClassic.getBlockCountInSector(sectorIndex)
                    // Read the block
                    val firstBlockIndex: Int = mifareClassic.sectorToBlock(sectorIndex)
                    val lastBlockIndex = firstBlockIndex.plus(sectorBlockCount)
                    for (sectorBlockIndex in firstBlockIndex until lastBlockIndex) {
                        Grove.e { "First Index: $firstBlockIndex, last index: $lastBlockIndex, sector block index $sectorBlockIndex, sector Index: $sectorIndex" }
                        val block: ByteArray = mifareClassic.readBlock(sectorBlockIndex)
                        if (!ByteUtils.isNullOrEmpty(block)) {
                            Grove.e { "Block is not empty" }
                            val hexValue = ByteUtils.byte2Hex(block)
                            Grove.e { "Hex value : $hexValue" }
                            ByteUtils.hex2Ascii(hexValue)?.let { bytesString ->
                                Grove.e { "Ascii value : $bytesString" }
                                values = values.plus(bytesString)
                            }
                        }
                    }
                } else {
                    Log.e("", "Auth Failed")
                }
            }

            mifareClassic.close()
            return values
        } catch (e: IOException) {
            e.printStackTrace()
            return e.toString()
            mifareClassic.close()
        }
    }

    private fun hexToASCII(hexValue: String): String {
        val output = java.lang.StringBuilder("")
        var i = 0
        while (i < hexValue.length) {
            val str = hexValue.substring(i, i + 2)
            output.append(str.toInt(16).toChar())
            i += 2
        }
        return output.toString()
    }

    fun ByteArray.toHexString(): String {
        return this.joinToString("") { it.toInt().and(0xff).toString(16).padStart(2, '0') }
    }

    @Throws(IOException::class, FormatException::class)
    fun writeData(
        tagId: String,
        message: NdefMessage
    ): Boolean {
        if (tagId != tagId) {
            return false
        }
        if (ndef != null) {
            ndef.connect()
            if (ndef.isConnected) {
                ndef.writeNdefMessage(message)
                return true
            }
        } else if (ndefFormatable != null) {
            ndefFormatable.connect()
            if (ndefFormatable.isConnected) {
                ndefFormatable.format(message)
                return true
            }
        }
        return false
    }

    @Throws(IOException::class)
    private fun close() {
        ndef?.close() ?: ndefFormatable?.close()
    }

    companion object {
        fun bytesToHexString(src: ByteArray): String? {
            if (ByteUtils.isNullOrEmpty(src)) {
                return null
            }
            val sb = StringBuilder()
            for (b in src) {
                sb.append(String.format("%02X", b))
            }
            return sb.toString()
        }
    }
}