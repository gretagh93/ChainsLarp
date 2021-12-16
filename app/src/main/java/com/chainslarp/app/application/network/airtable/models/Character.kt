package com.chainslarp.app.application.network.airtable.models

import com.chainslarp.app.application.network.airtable.AirtableRecord
import com.chainslarp.app.application.network.airtable.AirtableRecordResponse
import com.squareup.moshi.Json


data class CharacterFields(
    @field:Json(name="IDRFID") val id : String,
    @field:Json(name= "Name") val name : String,
    @field:Json(name="Notes") val notes : String,
    @field:Json(name="Type") val type : String,
    @field:Json(name="Timeline") val timeline : List<String>?,
    @field:Json(name="Quests")  val quests : List<String>?,
    val bio : String,
    @field:Json(name="Realidad") val reality : String,
    @field:Json(name="Ciudadana") val ciudadana : Int,
    @field:Json(name="Inocente") val inocente : Int,
    @field:Json(name="Sabia") val sabia : Int,
    @field:Json(name="Gobernante") val gobernante : Int,
    @field:Json(name="Heroína") val heroica : Int,
    @field:Json(name="Cuidadora") val cuidadora : Int,
    @field:Json(name="Creadora") val creadora : Int,
    @field:Json(name="Exploradora") val exploradora : Int,
    @field:Json(name="Bufón / Loco") val bufon : Int,
    @field:Json(name="Rebelde") val rebelde : Int,
    @field:Json(name="Amante") val amante : Int,
    @field:Json(name="Maga") val maga : Int)

typealias Character = AirtableRecord<CharacterFields>
typealias CharacterResponse = AirtableRecordResponse<Character>