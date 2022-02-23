package com.chains.larp.domain

import com.chains.larp.domain.character.Character
import com.chains.larp.domain.character.CharacterFields
import com.chains.larp.domain.character.CharacterUpdateRecord
import com.chains.larp.domain.models.Quest
import com.chains.larp.domain.models.QuestFields
import com.chains.larp.domain.models.QuestUpdateFields
import com.chains.larp.domain.models.QuestUpdateRecord
import com.minikorp.grove.Grove

interface AirtableRepository {
    /**
     * Retrieves a Character from the repository based on the given ID.
     */
    suspend fun fetchCharacter(id: String): Character

    /**
     * Updates both, the character fields and quest data on the NFC and if success on the repository.
     */
    suspend fun updateCharacter(id: String, fields: CharacterFields, completedQuests: Map<String, QuestFields>)

    /**
     * Fetches all quests.
     */
    suspend fun fetchCharacterQuests(characterId: String): List<Quest>
}

class AirtableRepositoryImpl(private val api: AirtableApi) : AirtableRepository {

    companion object {
        private const val AIRTABLE_BASE_ID = "YOUR ID"
    }

    override suspend fun fetchCharacter(id: String): Character {
        val isIdrfid = id.length == 7
        Grove.d { "Fetching by IDRFID: $isIdrfid" }
        val character = if (isIdrfid) api.filterCharacter(baseId = AIRTABLE_BASE_ID, filter = "{IDRFID}=$id").records.firstOrNull()
        else api.getCharacter(baseId = AIRTABLE_BASE_ID, characterId = id)

        if (character == null) throw NullPointerException()
        else return character
    }

    override suspend fun updateCharacter(id: String, fields: CharacterFields, completedQuests: Map<String, QuestFields>) {
        api.updateCharacter(baseId = AIRTABLE_BASE_ID, id, CharacterUpdateRecord(fields))
        completedQuests.forEach { (questId, fields) ->
            api.updateQuest(AIRTABLE_BASE_ID, questId, QuestUpdateRecord(QuestUpdateFields(fields.completed)))
        }
    }

    override suspend fun fetchCharacterQuests(characterId: String): List<Quest> =
        api.fetchCharacterQuests(AIRTABLE_BASE_ID, filter = "FIND('${characterId.toInt()}', {IDRFID})").records.sortedBy { it.fields.name }
}
