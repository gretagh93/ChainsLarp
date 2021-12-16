package com.chainslarp.app.application.network.airtable.models

import com.chainslarp.app.application.network.airtable.AirtableRecord
import com.chainslarp.app.application.network.airtable.AirtableRecordResponse
import com.squareup.moshi.Json


data class QuestFields(
    @field:Json(name="Name") val name : String,
    @field:Json(name="Notes") val notes : String,
    @field:Json(name="Parent") val parent : List<String>,
    @field:Json(name="Type") val type : String,
    @field:Json(name="Description") val description : String
)

typealias Quest = AirtableRecord<QuestFields>
typealias QuestResponse = AirtableRecordResponse<Quest>