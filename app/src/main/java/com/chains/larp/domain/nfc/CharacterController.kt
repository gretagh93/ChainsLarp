package com.chains.larp.domain.nfc

import android.app.Activity
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.nfc.NfcManager
import com.chains.larp.app.appContext
import com.chains.larp.domain.AirtableRepository
import com.chains.larp.domain.auth.AuthController
import com.chains.larp.domain.models.*
import com.minikorp.grove.Grove
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.withContext

data class CharacterTagInfo(
    val characterId: String,
    val seedId: String = "", //No needed for now, so we add it here as "" to avoid writting
    val gameId: String = "",
    val archetypes: List<UInt> = emptyList()
)

typealias CharacterTagRef = Pair<String, NfcTag>

data class NfcState(val isEnabled: Boolean = false)

interface CharacterController {

    suspend fun readCharacterTag(tag: NfcTag): Result<CharacterTagInfo>

    suspend fun loadCharacterData(characterId: String): Result<Character>

    suspend fun updateCharacterData(
        characterId: String,
        characterFields: CharacterFields,
        updatedQuests: Map<String, QuestFields>,
        currentReadingTag: NfcTag
    ): Result<Unit>

    fun enableCharacterNfcReader(enable: Boolean, activity: Activity): Result<NfcState>

    suspend fun loadQuests(): Result<List<Quest>>

    fun writeCharacterTag(
        characterId: String,
        fields: CharacterFields,
        tag: NfcTag
    ): Boolean

    suspend fun overrideCharacterTag(
        tagInfo: CharacterTagInfo,
        tag: NfcTag
    ): Result<Unit>
}

/**
 * Implementation of [AuthController] backed by Firebase and subscriptions API.
 */
class CharacterControllerImpl(private val repository: AirtableRepository) :
    CharacterController {

    private val scope = CoroutineScope(Job())
    private val nfcManager = appContext.getSystemService(Context.NFC_SERVICE) as NfcManager

    override suspend fun loadQuests(): Result<List<Quest>> =
        withContext(Dispatchers.IO) {
            runCatching {
                repository.fetchQuests()
            }
        }

    override suspend fun readCharacterTag(tag: NfcTag): Result<CharacterTagInfo> {
        val data = tag.readData()
        Grove.e { "Current readed tag: $data" }
        return if (data != null) {
            Result.success(data)
        } else {
            Result.failure(NullPointerException())
        }
        //   ID - secuencia 7 cifras que sera el ID unico de cada personaje. son los 7 primeros de la 2º fila del 1º bloque del TAG. El valor de correlacion de la BBDD es IDRFID
        //   IDLastSeed - Siguientes 5 numeros de la 2º fila del 1º bloque del TAG. Hay que hacer check con el ID de Events de la partida
    }

    override fun enableCharacterNfcReader(enable: Boolean, activity: Activity): Result<NfcState> {
        if (enable) {
            return try {
                val intent =
                    Intent(activity, activity.javaClass).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
                val nfcPendingIntent = PendingIntent.getActivity(activity, 0, intent, 0)
                nfcManager.defaultAdapter?.enableForegroundDispatch(
                    activity,
                    nfcPendingIntent,
                    null,
                    null
                )
                Result.success(NfcState(true))
            } catch (ex: Exception) {
                Result.failure(ex)
            }
        } else {
            return try {
                nfcManager.defaultAdapter?.disableForegroundDispatch(activity)
                Result.success(NfcState(false))
            } catch (ex: Exception) {
                Result.failure(ex)
            }
        }
    }

    override suspend fun loadCharacterData(characterId: String) =
        withContext(Dispatchers.IO) {
            runCatching {
                Grove.e { "Retrieve Character for user id $characterId" }
                val character = repository.fetchUser(characterId)
                Grove.e { " Character is is $character" }
                character
            }
        }

    override suspend fun updateCharacterData(
        characterId: String,
        characterFields: CharacterFields,
        updatedQuests: Map<String, QuestFields>,
        currentReadingTag: NfcTag
    ): Result<Unit> =
        withContext(Dispatchers.IO) {
            runCatching {
                Grove.i { "Trying to write NFC for $characterId" }
                val questMergedCharacterFields = characterFields.mergeWithQuestArchetypes(updatedQuests.values.toList())
                if (writeCharacterTag(characterId, questMergedCharacterFields, currentReadingTag)) {
                    repository.updateUser(characterId, questMergedCharacterFields, updatedQuests)
                } else {
                    throw TagNotInRangeException
                }
            }
        }

    override fun writeCharacterTag(
        characterId: String,
        fields: CharacterFields,
        tag: NfcTag
    ): Boolean {
        val nfcCharacterTagInfo = CharacterTagInfo(characterId, archetypes = fields.toArchetypesIntList().map { it.toUInt() })
        return tag.writeData(nfcCharacterTagInfo, false)
    }

    override suspend fun overrideCharacterTag(tagInfo: CharacterTagInfo, tag: NfcTag): Result<Unit> =
        withContext(Dispatchers.IO) {
            runCatching {
                if (!tag.writeData(tagInfo, true)) {
                    throw TagNotInRangeException
                }
            }
        }
}
