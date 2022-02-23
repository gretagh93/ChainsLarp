package com.chains.larp.domain

import com.chains.larp.domain.character.Character
import com.chains.larp.domain.character.CharacterResponse
import com.chains.larp.domain.character.CharacterUpdateRecord
import com.chains.larp.domain.character.CharactersResponse
import com.chains.larp.domain.models.*
import retrofit2.Response
import retrofit2.http.*

/**
 * Generic Airtable record model. Any Airtable result comes with an ID, a bunch of fields and a timestamp.
 */
data class AirtableRecord<T>(val id: String, val fields: T, val createdTime: String)

/**
 * Generic Airtable record model for multiple params. It contains a list of values and an offset if needed.
 */
data class AirtableRecordResponse<T>(val records: List<T>, val offset: String)

/**
 * Chains app Airtable API.
 * @see https://airtable.com/appKmqcWDJjiyYfwX/tblP0yzyyMmVkZd4D
 * @see https://airtable.com/appKmqcWDJjiyYfwX/api/docs#curl/introduction
 */
interface AirtableApi {
    @GET("v0/{baseId}/World")
    suspend fun filterCharacter(@Path("baseId") baseId: String,
                                @Query("filterByFormula") filter: String): CharactersResponse

    @GET("v0/{baseId}/World/{characterId}")
    suspend fun getCharacter(@Path("baseId") baseId: String,
                             @Path("characterId") characterId: String): Character

    @PATCH("v0/{baseId}/World/{characterId}")
    suspend fun updateCharacter(@Path("baseId") baseId: String,
                                @Path("characterId") characterId: String,
                                @Body fields: CharacterUpdateRecord): CharacterResponse

    @GET("v0/{baseId}/Quests")
    suspend fun fetchCharacterQuests(@Path("baseId") baseId: String,
                                     @Query("filterByFormula") filter: String): QuestResponse

    @PATCH("v0/{baseId}/Quests/{questId}")
    suspend fun updateQuest(@Path("baseId") baseId: String,
                            @Path("questId") questId: String,
                            @Body fields: QuestUpdateRecord): Response<Unit>

}