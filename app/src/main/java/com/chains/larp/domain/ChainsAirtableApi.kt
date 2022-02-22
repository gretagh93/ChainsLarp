package com.chains.larp.domain

import com.chains.larp.domain.auth.models.PlayerResponse
import com.chains.larp.domain.models.*
import retrofit2.Response
import retrofit2.http.*

data class AirtableRecord<T>(val id: String, val fields: T, val createdTime: String)

data class AirtableRecordResponse<T>(val records: List<T>, val offset: String)

interface AirtableApi {
    @GET("v0/{baseId}/World")
    suspend fun getPlayer(
        @Path("baseId") baseId: String,
        @Query("filterByFormula") filter: String
    ): CharactersResponse

    @PATCH("v0/{baseId}/World/{characterId}")
    suspend fun updatePlayer(
        @Path("baseId") baseId: String,
        @Path("characterId") characterId: String,
        @Body fields: CharacterUpdateRecord
    ): CharacterResponse

    @GET("v0/{baseId}/Quests")
    suspend fun getQuests(@Path("baseId") baseId: String): QuestResponse

    @PATCH("v0/{baseId}/Quests/{questId}")
    suspend fun updateQuest(
        @Path("baseId") baseId: String,
        @Path("questId") questId: String,
        @Body fields: QuestUpdateRecord
    ): Response<Unit>

    @GET("v0/{baseId}/People")
    suspend fun login(
        @Path("baseId") baseId: String,
        @Query("filterByFormula") filterByFormula: String
    ): PlayerResponse
}