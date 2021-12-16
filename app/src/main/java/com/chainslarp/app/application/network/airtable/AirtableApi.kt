package com.chainslarp.app.application.network.airtable

import com.chainslarp.app.application.network.airtable.models.CharacterResponse
import com.chainslarp.app.application.network.airtable.models.PlayerResponse
import com.chainslarp.app.application.network.airtable.models.QuestResponse
import com.chainslarp.app.application.network.airtable.models.ScenesResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

data class AirtableRecord<T>(val id : String, val fields : T, val createdTime : String)

data class AirtableRecordResponse<T>(val records : List<T>, val offset : String)

interface AirtableApi {
    @GET("/v0/{baseId}/Scenes")
    suspend fun getScenes(@Path("baseId") baseId: String): ScenesResponse

    @GET("/v0/{baseId}/World")
    suspend fun getPlayer(@Path("baseId") baseId: String,
                          @Query("view") views : String,
                          @Query("filterByFormula") filterFormula : String): CharacterResponse

    @GET("/v0/{baseId}/Quests") //Traer relacion entre PJ y Relationship, where quest es tipo QA
    suspend fun getQuests(@Path("baseId") baseId: String): QuestResponse
    //Cuando una mision se complete, actualizar los valores del personaje en base a los arqueotipos

    //Get personaje from NFT
    @GET("/v0/{baseId}/People")
    suspend fun login(@Path("baseId") baseId: String,
                      @Query("filterByFormula") filterFormula : String): PlayerResponse
}