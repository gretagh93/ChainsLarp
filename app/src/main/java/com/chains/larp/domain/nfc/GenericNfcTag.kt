package com.chains.larp.domain.nfc

import android.nfc.FormatException
import android.nfc.Tag
import android.nfc.tech.MifareClassic
import android.nfc.tech.Ndef
import android.nfc.tech.NdefFormatable
import android.util.Log
import com.chains.larp.domain.character.CharacterTagInfo
import com.chains.larp.utils.ByteUtils
import com.minikorp.grove.Grove
import java.io.IOException
import java.util.*

class GenericNfcTag @Throws(FormatException::class) constructor(val tag: Tag) {
    private val NDEF = Ndef::class.java.canonicalName
    private val NDEF_FORMATABLE = NdefFormatable::class.java.canonicalName
    private val MIFARE = MifareClassic::class.java.canonicalName

    private val ndef: Ndef?
    private val ndefFormatable: NdefFormatable?
    private val mifareClassic: MifareClassic?

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

    /**
     * Reads the data of an Chains NFC. This must be a Mifare classic NFC to work with the app.
     * The process is simple:
     * - Connect to the Tag
     * - Read ONLY the first block.
     * - Authenticate with the block and if success move on
     * - We make use only of the first 3 sectors of the first block(All sectors have 16bits)
     * -- 1º sector is skipped because it include Tag header related data
     * -- 2º sector contains 16 digits on hexadecimal format:
     * --- First 7 digits are the IDRFID
     * --- Next 5 are the game ID
     * --- Last 4 are the seed ID
     *
     * -- 3º sector contains the archetypes of the character on binary format. They come has a list of [UInt] values
     */
    fun readData(): CharacterTagInfo? {
        if (mifareClassic == null) return null
        try {
            var characterId = ""
            var seedId = ""
            var gameId = ""
            var archetypesList = emptyList<UInt>()

            //Variables -- Not needed for now because we only write on the first sector
            val sectorCount: Int = mifareClassic.sectorCount
            val tagSize: Int = mifareClassic.size

            //Keys
            var defaultKeys: ByteArray? = byteArrayOf()
            defaultKeys = MifareClassic.KEY_DEFAULT

            //Connecting to tag
            mifareClassic.connect()
            for (sectorIndex in 0 until 1) { //We only use the first sector
                val auth = mifareClassic.authenticateSectorWithKeyA(sectorIndex, defaultKeys)
                if (auth) {
                    // Read the block
                    val sectorBlockCount = mifareClassic.getBlockCountInSector(sectorIndex)

                    // Third would be the arqueotipes

                    val firstBlockIndex: Int = mifareClassic.sectorToBlock(sectorIndex)
                    val lastBlockIndex = firstBlockIndex.plus(sectorBlockCount)
                    for (sectorBlockIndex in firstBlockIndex..2) {
                        Grove.e { "First Index: $firstBlockIndex, last index: $lastBlockIndex, sector block index $sectorBlockIndex, sector Index: $sectorIndex" }
                        val block: ByteArray = mifareClassic.readBlock(sectorBlockIndex)
                        if (sectorBlockIndex == 0) continue //Skip header
                        if (sectorBlockIndex == 1) {
                            if (!ByteUtils.isNullOrEmpty(block) && block.size >= 15) {
                                Grove.e { "Block is not empty" }
                                characterId = String(block.slice(0..6).toByteArray()) //SSDIF
                                gameId = String(block.slice(7..11).toByteArray())
                                seedId = String(block.slice(12..15).toByteArray())
                                Grove.e { "First block character values : ID $characterId, game: $gameId, Seed: $seedId" }
                            } else {
                                Grove.e { "Expected character ids but got nothing" }
                            }
                        }

                        if (sectorBlockIndex == 2) {
                            if (!ByteUtils.isNullOrEmpty(block)) {
                                Grove.e { "Block is not empty" }
                                archetypesList = block.map { it.toUInt() }
                                Grove.e { "Second block character archetypes : $archetypesList" }
                            } else {
                                Grove.e { "Expected archetypes ids but got nothing" }
                            }
                        }
                    }
                } else {
                    Log.e("", "Auth Failed")
                }
            }
            return CharacterTagInfo(characterId, seedId, gameId, archetypesList)
        } catch (e: IOException) {
            e.printStackTrace()
            return null
        } finally {
            mifareClassic.close()
        }
    }

    /**
     * Writes the data of a Character on a Chains NFC. This must be a Mifare classic NFC to work with the app.
     * The process is simple:
     * - Connect to the Tag
     * - Write ONLY the first block.
     * - Authenticate with the block and if success move on
     * - We make use only of the first 3 sectors of the first block(All sectors have 16bits)
     * -- 1º sector is skipped because it include Tag header related data
     * -- 2º sector contains 16 digits on hexadecimal format:
     * --- First 7 digits are the IDRFID
     * --- Next 4 are the game ID
     * --- Last 5 are the seed ID
     * --- We concatenate the needed data from [CharacterTagInfo] and write it as a [ByteArray]
     *
     * -- 3º sector contains the archetypes of the character on binary format. They are write has a list of [UInt] values
     *
     * @param override Only used to override the full tag. This must only be use from the admin screen to write down empty tags before a game.
     */
    @Throws(IOException::class, FormatException::class)
    fun writeData(tagInfo: CharacterTagInfo,
                  override: Boolean = false): Boolean {
        if (mifareClassic == null) return false
        try {

            //Variables -- No needed because we only make use of the first block
            val sectorCount: Int = mifareClassic.sectorCount
            val tagSize: Int = mifareClassic.size

            //Keys
            var defaultKeys: ByteArray? = byteArrayOf()
            defaultKeys = MifareClassic.KEY_DEFAULT

            //Connecting to tag
            mifareClassic.connect()
            for (sectorIndex in 0 until 1) { //We only use the first sector
                val auth = mifareClassic.authenticateSectorWithKeyA(sectorIndex, defaultKeys)
                if (auth) {
                    val sectorBlockCount = mifareClassic.getBlockCountInSector(sectorIndex)

                    val firstBlockIndex: Int = mifareClassic.sectorToBlock(sectorIndex)
                    val lastBlockIndex = firstBlockIndex.plus(sectorBlockCount)
                    for (sectorBlockIndex in firstBlockIndex..2) {
                        Grove.e { "First Index: $firstBlockIndex, last index: $lastBlockIndex, sector block index $sectorBlockIndex, sector Index: $sectorIndex" }
                        if (sectorBlockIndex == 0) continue //Skip header & ID
                        if (sectorBlockIndex == 1 && override) { //Only write ID, seed and game if override
                            val mergeString = tagInfo.characterId + tagInfo.gameId + tagInfo.seedId
                            mifareClassic.writeBlock(sectorBlockIndex, mergeString.toByteArray())
                        }
                        if (sectorBlockIndex == 2) {
                            val archetypeByteArray = ByteArray(16)
                            tagInfo.archetypes.map { it.toByte() }
                                .forEachIndexed { index, byte -> archetypeByteArray[index] = byte }
                            mifareClassic.writeBlock(sectorBlockIndex, archetypeByteArray)
                        }
                    }
                } else {
                    Log.e("", "Auth Failed")
                }
            }
            return true
        } catch (e: IOException) {
            Grove.e(e) { "There was an error writting the tag" }
            return false
        } finally {
            close()
        }
    }

    @Throws(IOException::class)
    private fun close() {
        ndef?.close() ?: ndefFormatable?.close() ?: mifareClassic?.close()
    }
}