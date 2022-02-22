package com.chains.larp.domain

import com.chains.larp.domain.auth.models.Player
import com.chains.larp.domain.models.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job

class AirtableRepository(private val api: AirtableApi) : CoroutineScope by CoroutineScope(Job()) {

    companion object {
        private const val AIRTABLE_BASE_ID = "appKmqcWDJjiyYfwX"
    }

    suspend fun fetchUser(id: String, characterType: String = "Pjs"): Character {
        val character =
            api.getPlayer(baseId = AIRTABLE_BASE_ID, filter = "{IDRFID}=$id").records.firstOrNull()
        if (character == null) throw NullPointerException()
        else return character
    }

    suspend fun updateUser(id: String, fields: CharacterFields, completedQuests: Map<String, QuestFields>) {
        api.updatePlayer(baseId = AIRTABLE_BASE_ID, id, CharacterUpdateRecord(fields))
        completedQuests.forEach { (questId, fields) ->
            api.updateQuest(
                baseId = AIRTABLE_BASE_ID,
                questId,
                QuestUpdateRecord(QuestUpdateFields(fields.completed))
            )
        }
    }

    suspend fun fetchQuests(): List<Quest> = api.getQuests(AIRTABLE_BASE_ID).records

    suspend fun loginUser(username: String, password: String): Player =
        api.login(
            baseId = AIRTABLE_BASE_ID,
            filterByFormula = "AND({User} = '$username' {Pwd} = '$password')"
        ).records.firstOrNull() ?: throw LoginException()
}

class LoginException : Exception()
class CharacterNotFound(id: String) : Exception()
