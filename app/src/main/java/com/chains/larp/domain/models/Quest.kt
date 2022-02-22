package com.chains.larp.domain.models

import com.chains.larp.domain.AirtableRecord
import com.chains.larp.domain.AirtableRecordResponse
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

data class QuestUpdateRecord(val fields: QuestUpdateFields)

data class QuestUpdateFields(val Done: Boolean)

data class QuestFields(
    @field:Json(name = "Name") val name: String,
    @field:Json(name = "Notes") val notes: String,
    @field:Json(name = "Mains") val characterIdRelated: List<String>,
    @field:Json(name = "Type") val type: String,
    @field:Json(name = "Ciudadana") val ciudadana: Int,
    @field:Json(name = "Inocente") val inocente: Int,
    @field:Json(name = "Sabia") val sabia: Int,
    @field:Json(name = "Gobernante") val gobernante: Int,
    @field:Json(name = "Heroína") val heroica: Int,
    @field:Json(name = "Cuidadora") val cuidadora: Int,
    @field:Json(name = "Creadora") val creadora: Int,
    @field:Json(name = "Exploradora") val exploradora: Int,
    @field:Json(name = "Bufón / Loco") val bufon: Int,
    @field:Json(name = "Rebelde") val rebelde: Int,
    @field:Json(name = "Amante") val amante: Int,
    @field:Json(name = "Maga") val maga: Int,
    @field:Json(name = "Done") val completed: Boolean
)

typealias Quest = AirtableRecord<QuestFields>
typealias QuestResponse = AirtableRecordResponse<Quest>